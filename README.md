# Sportbook-backend

## 📋 Requisitos previos

- [Docker](https://www.docker.com/) y [Docker Compose](https://docs.docker.com/compose/) instalados.
- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) 
- [Maven](https://maven.apache.org/) 
---

## ⚙️ Setup Local Environment

### 1. Levantar base de datos local con Docker

1. Desde el root del proyecto, ejecutar:

   ```bash
   docker compose up
   ```
Esto levanta un contenedor con PostgreSQL expuesto en el puerto 5432.

### 2. Variables de entorno
1. Configurar las siguientes variables de entorno

| Variable               | Requerida | Descripción                               | Ejemplo                                         |
| ---------------------- | :-------: | ----------------------------------------- | ----------------------------------------------- |
| `POSTGRES_DB_DRIVER`   |     No    | Driver JDBC para PostgreSQL.              | `org.postgresql.Driver`                         |
| `POSTGRES_DB_URL`      |     No    | URL JDBC a la base de datos PostgreSQL.   | `jdbc:postgresql://localhost:5432/sportbook_db` |
| `POSTGRES_DB_USER`     |     No    | Usuario de la base de datos.              | `postgres`                                      |
| `POSTGRES_DB_PASSWORD` |     No    | Password del usuario de la base de datos. | `postgres`                                      |


Si no definís estas variables, la app se levanta con H2 en memoria.

