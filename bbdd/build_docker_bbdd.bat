@echo off

REM Construir la imagen
docker build -t easytakeaway-db:latest .

REM Crear una red
docker network create easytakeaway-network

REM Ejecutar el contenedor
docker run --name easytakeaway-db --network easytakeaway-network -p 3307:3306 -d easytakeaway-db:latest

pause