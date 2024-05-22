@echo off

REM Construir la imagen
docker build -t easytakeaway-db:latest .

REM Ejecutar el contenedor
docker run --name easytakeaway-db -p 3307:3306 -d easytakeaway-db:latest

pause