Golden Rose - Backend de Microservicios
=======================================

Servicios y puertos
- Autenticación: 8001 (`Autentificacion`)
- Carrito: 8002 (`Carrito`)
- Usuarios: 8003 (`Usuario`)
- Catálogo: 8004 (`Catalogo`) – categorías, fallback de productos y lectura
- Inventario: 8005 (`Inventario`)
- Órdenes: 8006 (`Ordenes`)
- Pagos: 8007 (`Pagos`)
- Productos: 8008 (`Productos`, CRUD de skins/productos)

Perfiles
- `dev`: H2 en memoria.
- `prod`: MySQL (RDS o contenedor compose).

Arranque rápido (Linux/EC2)
```
cd golden-rose-backend
chmod +x start-all.sh
./start-all.sh
```
Windows: `powershell -ExecutionPolicy Bypass -File .\start-all.ps1`

Docker Compose
```
cd golden-rose-backend
docker compose build
docker compose up -d
```
Incluye MySQL, todos los servicios y puertos 8001-8008 expuestos.

OpenAPI/Swagger
`http://<host>:<puerto>/swagger-ui/index.html` en cada servicio.

Catálogo y Productos
- El alta/edición se hace en `Productos` (8008). `Catalogo` redirige (HTTP 307) para no duplicar lógica.
- Catálogo lee productos de `Productos` mediante `PRODUCTO_SERVICE_URL`.

Crear un producto (solo URL de imagen)
`POST http://<host>:8008/api/productos`
```json
{
  "nombre": "Vandal Champions 2025",
  "descripcion": "Edición especial",
  "rareza": "Exclusive",
  "precio": 49.99,
  "imagenUrl": "https://ejemplo.com/vandal.png",
  "stock": 25
}
```
Actualizar: `PUT /api/productos/{id}` con el mismo cuerpo. Campos mínimos: `nombre`, `precio`, `imagenUrl` y `stock`.

Integración frontend/mobile
- Usar `VITE_API_PRODUCTO` (puerto 8008) como fuente principal de productos. Catálogo (8004) queda como fallback de lectura.
- Imágenes: consumir directamente `imagenUrl`.

Tests
```
./mvnw test          # en cada microservicio
```
