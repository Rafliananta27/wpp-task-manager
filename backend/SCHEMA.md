# Schema
## boards
| Column | SQL type (MariaDB/MySQL) | Rules |
| --- | --- | --- |
| id | BIGINT | Primary key, auto increment, not null |
| name | VARCHAR(120) | Not null; trimmed length > 0 |
| created_at | TIMESTAMP(6) | Not null; default current timestamp |

## tasks
| Column | SQL type (MariaDB/MySQL) | Rules |
| --- | --- | --- |
| id | BIGINT | Primary key, auto increment, not null |
| board_id | BIGINT | Not null; FK to boards.id; ON DELETE CASCADE |
| title | VARCHAR(200) | Not null; trimmed length > 0 |
| description | TEXT | Nullable |
| status | VARCHAR(20) | Not null; default TODO; CHECK in TODO, IN_PROGRESS, DONE |
| created_at | TIMESTAMP(6) | Not null; default current timestamp |
| updated_at | TIMESTAMP(6) | Not null; default current timestamp; updated on modification |

Names and titles are not unique because duplicate labels are legitimate. IDs are the only unique constraints. The service rejects Java whitespace-only labels; SQL TRIM checks additionally reject empty and space-only strings on direct writes. MySQL TRIM does not remove every Unicode whitespace character, so application validation is stricter.

Index `idx_tasks_board_status(board_id, status)` supports listing tasks by board and filtering by status. Its leftmost board_id also supports unfiltered board lookup and the foreign key. Primary key indexes are automatic. No status-only index: tasks are always retrieved within a board.

CASCADE was selected because tasks belong exclusively to their board; deleting it removes its contents without orphaned data. The schema enforces this even for direct SQL deletes. Flyway creates and versions the schema automatically on startup.

Rejected option: a status lookup table would add joins and seed data for only three fixed values; a CHECK constraint is sufficient. Soft deletes were considered but omitted because retention and restoration are not part of this product.
