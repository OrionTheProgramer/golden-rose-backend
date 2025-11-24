# Inicia todos los microservicios en ventanas separadas
# Puertos esperados: auth 8001, carrito 8002, usuarios 8003, catalogo 8004, inventario 8005, ordenes 8006, pagos 8007, productos 8008

$services = @(
    @{ Name = 'Autentificacion'; Path = 'Autentificacion'; Cmd = '.\\mvnw spring-boot:run' },
    @{ Name = 'Carrito';           Path = 'Carrito';           Cmd = '.\\mvnw spring-boot:run' },
    @{ Name = 'Usuario';           Path = 'Usuario';           Cmd = '.\\mvnw spring-boot:run' },
    @{ Name = 'Catalogo';          Path = 'Catalogo';          Cmd = '.\\mvnw spring-boot:run' },
    @{ Name = 'Inventario';        Path = 'Inventario';        Cmd = '.\\mvnw spring-boot:run' },
    @{ Name = 'Ordenes';           Path = 'Ordenes';           Cmd = '.\\mvnw spring-boot:run' },
    @{ Name = 'Pagos';             Path = 'Pagos';             Cmd = '.\\mvnw spring-boot:run' },
    @{ Name = 'Productos';         Path = 'Productos';         Cmd = '.\\mvnw spring-boot:run' }
)

foreach ($svc in $services) {
    $workdir = Join-Path $PSScriptRoot $svc.Path
    Write-Host "Iniciando $($svc.Name) en $workdir"
    Start-Process powershell -WorkingDirectory $workdir -ArgumentList "-NoExit", "-Command", $svc.Cmd
}
