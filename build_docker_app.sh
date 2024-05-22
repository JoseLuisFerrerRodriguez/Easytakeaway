#!/bin/bash

# Construir la imagen Docker
docker build -t easytakeaway-app:v1 .

# Ejecutar el contenedor Docker
docker run --name easytakeaway-app --network easytakeaway-network -dp 8081:8080 easytakeaway-app:v1