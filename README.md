# Appointment Management System

## Prerequisites

- postgresql instance. Using docker, a sample container to run. Adjust credentials in the app and instance as wanted.

```sh
# Adjust credentials as wanted
docker container run --name ams-postgres -e POSTGRES_PASSWORD=p -e POSTGRES_USER=u -e POSTGRES_DB=ams_db -p 5432:5432 -d postgres:16
```

- Java 21
- maven 3.9.9 (wrapper could be used)

## Assumptions

- Work hours are limited (configured in application.yaml)
- Slot of appointment is one hour started at exactly specific hour like 10:00 - 11:00 - 12:00 - 13:00 and so on.
- Patient is identified by first and last name only for simplicity with sample data provided

## Running

- Postman collection for sample requests in `ams.postman_collection.json`
- Swagger UI available after running the app at http://localhost:8085/swagger-ui/index.html