#!/usr/bin/env bash
set -euo pipefail

# Arranca todos los microservicios en segundo plano (pensado para Linux/EC2).
# Puertos: auth 8001, carrito 8002, usuarios 8003, catalogo 8004, inventario 8005, ordenes 8006, pagos 8007, productos 8008

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

services=(
  "Autentificacion"
  "Carrito"
  "Usuario"
  "Catalogo"
  "Inventario"
  "Ordenes"
  "Pagos"
  "Productos"
)

mkdir -p "$BASE_DIR/logs"

for svc in "${services[@]}"; do
  svc_dir="$BASE_DIR/$svc"
  log_file="$BASE_DIR/logs/${svc,,}.log"

  echo "Iniciando $svc ..."
  (cd "$svc_dir" && chmod +x mvnw && nohup ./mvnw spring-boot:run > "$log_file" 2>&1 &) 
done

echo "Todos los servicios fueron lanzados. Revisa los logs en $BASE_DIR/logs"
