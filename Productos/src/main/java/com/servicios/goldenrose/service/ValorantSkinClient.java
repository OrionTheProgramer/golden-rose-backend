package com.servicios.goldenrose.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cliente ligero contra valorant-api.com para autocompletar skins y rarezas.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValorantSkinClient {

    private static final String SKINS_URL = "https://valorant-api.com/v1/weapons/skins?language=es-ES";
    private static final String TIERS_URL = "https://valorant-api.com/v1/contenttiers?language=es-ES";

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /** cache uuid -> tier icon/name */
    private final Map<String, TierInfo> tierCache = new ConcurrentHashMap<>();

    public Optional<SkinData> findSkin(String nombre, String referenciaUuid) {
        try {
            ensureTierCache();
            JsonNode data = fetchJson(SKINS_URL).path("data");
            if (!data.isArray()) return Optional.empty();

            List<JsonNode> skins = new ArrayList<>();
            data.forEach(skins::add);

            JsonNode matched = null;
            if (referenciaUuid != null && !referenciaUuid.isBlank()) {
                matched = skins.stream()
                        .filter(n -> referenciaUuid.equalsIgnoreCase(n.path("uuid").asText()))
                        .findFirst()
                        .orElse(null);
            }

            String objetivo = normalize(nombre);
            if (matched == null) {
                matched = skins.stream()
                        .filter(n -> {
                            String norm = normalize(n.path("displayName").asText());
                            return norm.contains(objetivo) || objetivo.contains(norm);
                        })
                        .min(Comparator.comparingInt(n -> {
                            String norm = normalize(n.path("displayName").asText());
                            return Math.abs(norm.length() - objetivo.length());
                        }))
                        .orElse(null);
            }

            if (matched == null) return Optional.empty();

            String tierUuid = matched.path("contentTierUuid").asText(null);
            TierInfo tier = tierUuid != null ? tierCache.get(tierUuid) : null;
            String imageUrl = pickImage(matched);

            return Optional.of(new SkinData(
                    matched.path("displayName").asText(),
                    imageUrl,
                    tier != null ? tier.getName() : null,
                    tier != null ? tier.getIcon() : null
            ));
        } catch (Exception e) {
            log.warn("No se pudo resolver skin desde valorant-api: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<ImageData> downloadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return Optional.empty();
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<byte[]> res = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (res.statusCode() >= 200 && res.statusCode() < 300) {
                String contentType = res.headers().firstValue("content-type").orElse("image/png");
                return Optional.of(new ImageData(res.body(), contentType));
            }
        } catch (Exception e) {
            log.warn("No se pudo descargar imagen {}: {}", imageUrl, e.getMessage());
        }
        return Optional.empty();
    }

    private JsonNode fetchJson(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new IllegalStateException("Respuesta no valida de valorant-api: " + res.statusCode());
        }
        return mapper.readTree(res.body());
    }

    private String pickImage(JsonNode skin) {
        if (skin.hasNonNull("fullRender")) return skin.get("fullRender").asText();
        if (skin.hasNonNull("displayIcon")) return skin.get("displayIcon").asText();
        JsonNode chromas = skin.path("chromas");
        if (chromas.isArray() && chromas.size() > 0) {
            JsonNode ch = chromas.get(0);
            if (ch.hasNonNull("fullRender")) return ch.get("fullRender").asText();
            if (ch.hasNonNull("displayIcon")) return ch.get("displayIcon").asText();
        }
        return null;
    }

    private void ensureTierCache() {
        if (!tierCache.isEmpty()) return;
        try {
            JsonNode data = fetchJson(TIERS_URL).path("data");
            if (data.isArray()) {
                data.forEach(node -> {
                    String uuid = node.path("uuid").asText();
                    String name = node.path("displayName").asText(null);
                    String icon = node.path("displayIcon").asText(null);
                    if (uuid != null) {
                        tierCache.put(uuid, new TierInfo(uuid, name, icon));
                    }
                });
            }
        } catch (Exception e) {
            log.warn("No se pudo cargar content tiers: {}", e.getMessage());
        }
    }

    private String normalize(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
        return normalized.replaceAll("[^a-z0-9]", "");
    }

    @Getter
    @RequiredArgsConstructor
    public static class TierInfo {
        private final String uuid;
        private final String name;
        private final String icon;
    }

    public record SkinData(String name, String imageUrl, String tierName, String tierIconUrl) {
    }

    public record ImageData(byte[] data, String contentType) {
    }
}
