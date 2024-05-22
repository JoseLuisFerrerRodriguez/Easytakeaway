#!/bin/bash

# Construir la imagen Docker
docker build -t easytakeaway-db:latest .

# Crear una red
docker network create easytakeaway-network

# Ejecutar el contenedor Docker
docker run --name easytakeaway-db -p 3307:3306 -d easytakeaway-db:latest