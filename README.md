Golden Rose - Backend de Microservicios
=======================================

Servicios y puertos
- Autenticación: 8001 (`Autentificacion`)
- Carrito: 8002 (`Carrito`)
- Usuarios: 8003 (`Usuario`)
- Catálogo: 8004 (`Catalogo`) – categorías y fallback de productos
- Inventario: 8005 (`Inventario`)
- Órdenes: 8006 (`Ordenes`)
- Pagos: 8007 (`Pagos`)
- Productos dedicado: 8008 (`Productos`, recomendado para skins Valorant)

Perfiles
- `dev`: H2 en memoria, arranca rápido para pruebas locales.
- `prod`: MySQL RDS 

Arranque rápido (Linux/EC2)
```
cd golden-rose-backend
chmod +x start-all.sh
./start-all.sh
```
Logs en `golden-rose-backend/logs`.

Arranque en Windows
```
cd golden-rose-backend
powershell -ExecutionPolicy Bypass -File .\start-all.ps1
```

OpenAPI/Swagger
`/swagger-ui.html` en cada microservicio.

Microservicio Productos (8008) – skins Valorant
- CRUD REST en `/api/productos`
- Almacena imagen embebida (BLOB) o `imagenUrl`
- Campo opcional `referenciaExterna` para traer datos de https://valorant-api.com (nombre/icono/rareza) si se envía un `skinlevel uuid`.
- Endpoint de imagen: `/api/productos/{id}/imagen` (devuelve bytes o redirige a `imagenUrl`).
- Semilla dev: `data.sql` carga “Vandal Champions 2025” con imagen remota y referencia externa.

Crear/actualizar productos
1) Multipart (imagen binaria)
```
POST http://<host>:8008/api/productos
Content-Type: multipart/form-data
nombre=Vandal Reaver
precio=45.5
rareza=Exclusive
categoria=Rifle
descripcion=Skin mítica
referenciaExterna=bc8d1f88-4d76-8f70-8e3c-5dc63afcdd19   # (uuid opcional de valorant-api.com)
imagen=@reaver_vandal.png                               # archivo opcional
```
Actualizar: `PUT /api/productos/{id}` con los mismos campos.

2) JSON (base64)
```
POST http://<host>:8008/api/productos
Content-Type: application/json
{
  "nombre": "Phantom Oni",
  "descripcion": "Edición limitada",
  "precio": 39.9,
  "rareza": "Exclusive",
  "categoria": "Rifle",
  "imagenUrl": "https://...",
  "imagenBase64": "<base64 de la png/jpg>",
  "imagenContentType": "image/png",
  "referenciaExterna": "f048e0fa-4de4-2729-21e6-bad2a1421d00"
}
```

Notas de integración front/mobile
- Front y app móvil consultan primero `VITE_API_PRODUCTO`/`ApiConfigMobile.producto`, luego usan Catálogo (8004) como respaldo.
- Para mostrar imágenes usar `/api/productos/{id}/imagen` si `hasImageData=true`; si no, usar `imagenUrl`.
- Los puertos 8001-8008 deben estar abiertos en la VM/EC2 junto con 3306 para MySQL.

Tests
```
./mvnw test          # dentro de cada microservicio
```
