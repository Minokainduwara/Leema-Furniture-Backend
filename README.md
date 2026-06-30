# Leema Furniture Backend

A RESTful backend API for the **Leema Furniture** e-commerce/management platform, built with **Spring Boot** and **Java 21**.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL |
| Build Tool | Maven (Maven Wrapper included) |
| Utilities | Lombok |

---

## Prerequisites

Before running this project, make sure you have the following installed:

- **Java 21** or higher
- **Maven 3.9+** (or use the included `mvnw` wrapper)
- **MySQL** database server

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Minokainduwara/Leema-Furniture-Backend.git
cd Leema-Furniture-Backend
```

### 2. Configure the database

Create a MySQL database and update your `src/main/resources/application.properties` with your credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/leema_furniture
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Build the project

```bash
# Using Maven Wrapper (no Maven installation required)
./mvnw clean install        # Linux/macOS
mvnw.cmd clean install      # Windows
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The server will start at `http://localhost:8080` by default.

---

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/demo/
│   │       ├── controller/     # REST API controllers
│   │       ├── service/        # Business logic layer
│   │       ├── repository/     # Spring Data JPA repositories
│   │       └── model/          # JPA entity classes
│   └── resources/
│       └── application.properties
└── test/
    └── java/                   # Unit and integration tests
```

---

## Dependencies

- `spring-boot-starter-webmvc` — Spring MVC web layer
- `spring-boot-starter-data-jpa` — JPA/Hibernate ORM
- `mysql-connector-j` — MySQL JDBC driver
- `lombok` — Reduces boilerplate (getters, setters, constructors)
- `spring-boot-devtools` — Hot reload during development

---

## Running Tests

```bash
./mvnw test
```

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## License

This project is currently unlicensed. Contact the repository owner for usage permissions.
