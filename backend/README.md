# Backend
## Stack choice
Java with Spring Boot was selected because I have used Java and Spring Boot previously. MariaDB through XAMPP was selected because I am familiar with MySQL-style relational database design and queries, and it was already available locally. The application was tested against MariaDB 10.4.32. JDBC keeps the schema and database delete behaviour explicit instead of relying on ORM defaults.

## Prerequisites
- JDK 17
- Maven 3.9+
- MariaDB 10.4.32 (tested through XAMPP). The SQL also targets MySQL 8.0.16+; MySQL was not tested in this session.
- Internet access for the first Maven dependency download

## Setup from a clean clone
1. Start MySQL locally on port 3306.
2. Open phpMyAdmin or your SQL client as a local administrator, open `database-setup.sql`, and run it. Alternatively, from the backend directory: `mysql -u root -p < database-setup.sql`.
3. In this directory, run `mvn spring-boot:run`.
4. The API starts at `http://localhost:8081/api`. Check `http://localhost:8081/api/boards`.

Flyway automatically executes `src/main/resources/db/migration/V1__create_boards_and_tasks.sql` against an empty database during startup and tracks it in `flyway_schema_history`. Do not manually execute the migration as well. Tables and records persist in MySQL after application restarts. Do not edit an already-applied migration; add another migration for schema changes.

Defaults are development-only credentials defined in `application.properties`. No .env loader is used by Spring. Override values with OS environment variables if needed:

| Variable | Default |
| --- | --- |
| DB_URL | jdbc:mysql://localhost:3306/task_manager?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true |
| DB_USERNAME | task_user |
| DB_PASSWORD | local_task_password |
| PORT | 8081 |
| FRONTEND_ORIGIN | http://localhost:5173 |

For PowerShell: `$env:DB_PASSWORD = "your-local-password"`, then `mvn spring-boot:run`. Ensure the MySQL user's password matches.

## Tests
From this directory: `mvn test`.
Tests instantiate the service with Mockito repositories. No web server or database is needed. They cover creation, filtering, deletion delegation, blank/missing/overlong inputs, invalid statuses and missing resources. Foreign key rejection, status constraint rejection, cascade deletion and migration against an empty database were also checked manually against MariaDB; see the root verification checklist. The candidate ran all 29 tests successfully on Windows.

## Structure
`web` maps HTTP requests and exceptions; `service` handles validation and business rules without HTTP concepts; `repository` uses parameterised SQL; `model` contains immutable response records. Service operations are transactional.

See [API.md](API.md) and [SCHEMA.md](SCHEMA.md).

## Assumptions
Names need not be unique. Names/titles are stripped at both ends; descriptions are preserved. Limits: board name 120, task title 200, description 10000 Java characters. Statuses are case-sensitive; any of the three may transition to either of the others. Tasks cannot move between boards. Lists are ordered by increasing ID. Timestamps are returned as UTC instants. Deleting an absent resource returns 404, including repeated deletes. Schema creation is local and free.

## Trade-offs
Database CASCADE removes all child tasks atomically. No audit trail or undo is provided. We use a small fixed set of statuses with a CHECK constraint rather than a lookup table. No pagination or production hardening; concurrent writes are outside scope. Setup assumes a local MySQL installation, not Docker. See root README for current validation limitations.

## Build and run the JAR
Run `mvn package`, then `java -jar target/task-manager-0.0.1-SNAPSHOT.jar`. Stop any existing backend first to free port 8081. Both packaging and JAR startup were verified on the candidate's machine.

If Maven is not on PATH, the candidate used this PowerShell command (adjust the installation path for your machine):
```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2022.3.3\plugins\maven\lib\maven3\bin\mvn.cmd" package
```
