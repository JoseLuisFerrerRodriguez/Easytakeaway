FROM mysql:latest

ENV MYSQL_ROOT_PASSWORD=proyecto

COPY init.sql /docker-entrypoint-initdb.d/

EXPOSE 3306