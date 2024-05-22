@echo off

REM Construir la imagen
docker build -t easytakeaway-app:v1 .

REM Ejecutar el contenedor
docker run --name easytakeaway-app --network easytakeaway-network -dp 8081:8080 easytakeaway-app:v1

pause