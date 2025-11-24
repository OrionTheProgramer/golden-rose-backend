Golden Rose – Backend de Microservicios
=======================================

Servicios y puertos
- Autentificación: 8001 (`Autentificacion`)
- Carrito: 8002 (`Carrito`)
- Usuarios: 8003 (`Usuario`)
- Catálogo: 8004 (`Catalogo`) — categorías y fallback de productos
- Inventario: 8005 (`Inventario`)
- Órdenes: 8006 (`Ordenes`)
- Pagos: 8007 (`Pagos`)
- Productos dedicado (opcional): 8008 (configurado vía `VITE_API_PRODUCTO` en el front)

Arranque rápido (Linux/EC2)
```
cd golden-rose-backend
chmod +x start-all.sh
./start-all.sh
```
Registros: `logs/*.log`

Arranque en Windows
```
cd golden-rose-backend
powershell -ExecutionPolicy Bypass -File .\start-all.ps1
```

Perfiles
- `dev`: usa H2 en memoria.
- `prod`: apuntar a las bases reales (ver `application-prod.properties` en cada servicio).

Dependencias clave
- Spring Boot 3.4.6
- JPA/Hibernate
- Security (modo abierto por defecto, listo para JWT/gateway)
- Springdoc OpenAPI (Swagger UI en `/swagger-ui.html`)

Tests
```
./mvnw test
```
(ejecutar dentro de cada microservicio).

Notas
- Autentificación devuelve `id`, `role`, `token` para que el front use `user.id` en carrito, órdenes, etc.
- Catálogo soporta imágenes embebidas (`/api/productos/{id}/imagen`) y/o `imagenUrl`.
- Scripts `start-all.sh` y `start-all.ps1` lanzan todos los servicios en paralelo.
