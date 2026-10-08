# Mini Task Manager
A take-home assessment for WPP Media: boards and tasks, built as two independently runnable services.

- [Backend setup](backend/README.md): Java 17, Spring Boot, MariaDB, REST API on port 8081.
- [Frontend setup](frontend/README.md): React and Vite on port 5173.
- [API contract](backend/API.md)
- [Database schema](backend/SCHEMA.md)
- [Verification checklist](VERIFY.md)

There is no shared source code, combined build or server-rendered UI. The React application communicates with the backend only over HTTP.

## Verification status
Validated on the candidate's Windows machine on 7 October 2026 with Java 17.0.7, Node 22.18.0 and XAMPP MariaDB 10.4.32:
- Backend unit tests: 29 passed, 0 failures/errors/skipped.
- Maven package: BUILD SUCCESS; packaged JAR started and served the frontend successfully.
- Vite production build: passed.
- Manual checks: board/task creation, status persistence and filtering, deletion, data persistence after backend restart, API without frontend, unavailable-backend Retry and recovery, whitespace board name (400), missing board (404), invalid status filter (400).
- After board deletion, an orphan-task query returned no rows. Direct SQL inserts with a nonexistent board or invalid status were rejected by the database.
- Startup against a separate empty database created boards, tasks and flyway_schema_history successfully.
- The project was extracted into a new folder and dependency installation, builds and startup were checked again. A separate clean-machine setup has not been performed; all reported checks were on the candidate's Windows machine.

## Scope and trade-offs
Core board/task workflows are implemented in source. Authentication, cloud deployment, pagination, bulk import and task title/description editing are omitted. Frontend component tests are omitted; backend service tests take priority. HTTP and real MySQL integration checks are currently manual, so the mocked unit tests alone do not prove that SQL constraints work. The application assumes a single trusted user and a small data set.

## AI assistance
AI assisted scaffolding, code and documentation. I reviewed the implementation and ran the tests and manual checks reported above. AI-assisted output remains my responsibility, including the technical decisions, tests and schema.

## With another week
I would first automate the database checks for foreign keys, cascade deletion and migrations, and add HTTP integration tests for validation and consistent error responses. I would then add a small set of frontend tests for loading, empty and unavailable-backend states. Finally, I would verify the setup instructions on a second machine and refine them using that feedback.
