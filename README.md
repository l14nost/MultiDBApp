# Multi Database Application

A **Spring Boot** application that connects to **multiple databases** (PostgreSQL, MySQL, Oracle, and others) simultaneously, aggregates data, and provides a REST API to fetch the full list or a filtered set of elements.

---

## Features

- Connect to **any number of databases** at the same time
- Supports **PostgreSQL, MySQL, Oracle** (easily extendable)
- REST API to retrieve aggregated data
- Filtering via query parameters
- Swagger UI for easy API testing

---

## Technologies

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Data JPA / JDBC**
- **PostgreSQL / MySQL / Oracle**
- **Lombok**
- **OpenAPI / Swagger**

---

## Installation & Run

### 1. Clone the repository
```bash
git clone https://github.com/l14nost/MultiDBApp.git
cd multidb-app
```

### 2. Configure databases
Edit the **application.yml** file to specify your databases:
```yaml
db:
  data-sources:
    - name: db-1
      strategy: postgres
      url: jdbc:postgresql://localhost:5432/db1
      table: users
      user: postgres
      password: postgres
      mapping:
        id: id
        username: username_test
        name: first_name
        surname: last_name

    - name: db-2
      strategy: mysql
      url: jdbc:mysql://localhost:3306/db2
      table: users
      user: postgres
      password: postgres
      mapping:
        id: id
        username: username_test
        name: first_name
        surname: last_name
```

### 3. Build project 
```bash
mvn clean install
```

### 4. Run the application
```bash
mvn spring-boot:run
```