Golden Rose - Backend de Microservicios
=======================================

Servicios y puertos
- Autenticacion (8001) - login/registro y emision de JWT.
- Carrito (8002) - carrito por usuario y calculo de total.
- Usuarios (8003) - administracion de cuentas basicas y roles.
- Catalogo (8004) - lectura de productos/categorias y fallback de imagenes.
- Inventario (8005) - stock y umbrales criticos por producto.
- Ordenes (8006) - creacion de ordenes y actualizacion de estados.
- Pagos (8007) - pagos simulados y cambios de estado.
- Productos (8008) - CRUD principal de productos/skins.

Como correr todo
- Linux/macOS: `chmod +x start-all.sh && ./start-all.sh`
- Windows: `powershell -ExecutionPolicy Bypass -File .\start-all.ps1`
- Docker Compose: `docker compose build && docker compose up -d` (levanta MySQL + todos los servicios).

Swagger / OpenAPI
`http://<host>:<puerto>/swagger-ui/index.html` en cada servicio (p.ej. http://localhost:8008/swagger-ui/index.html).

Catalogo vs Productos
- Crear/editar productos se hace en `Productos` (8008). `Catalogo` redirige al 8008 para no duplicar logica.
- `Catalogo` mantiene lectura y fallback de imagenes/base64.

Crear un producto (JSON)
`POST http://<host>:8008/api/productos`
```json
{
  "nombre": "Vandal Champions 2025",
  "descripcion": "Edicion especial",
  "rareza": "Exclusive",
  "precio": 49.99,
  "imagenUrl": "https://ejemplo.com/vandal.png",
  "stock": 25
}
```
Actualizar: `PUT /api/productos/{id}` con el mismo cuerpo. Campos minimos: `nombre`, `precio`, `imagenUrl`, `stock`.

Integracion frontend/mobile
- Usar `VITE_API_PRODUCTO` (puerto 8008) como fuente principal de productos.
- `Catalogo` (8004) queda como fallback de lectura y para servir imagenes guardadas.

Perfiles de Spring
- `dev`: H2 en memoria.
- `prod`: MySQL (RDS o contenedor de compose).

Tests
- En cada microservicio: `./mvnw test`
