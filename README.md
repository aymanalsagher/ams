# Appointment Management System

## Prerequisites

- postgresql instance. Using docker, a sample container to run. Adjust credentials in the app and instance as wanted.

```sh
# Adjust credentials as wanted
docker container run --name ams-postgres -e POSTGRES_PASSWORD=p -e POSTGRES_USER=u -e POSTGRES_DB=ams_db -p 5432:5432 -d postgres:16
```

- Java 21
- maven 3.9.9 (wrapper could be used)