# REST API
Base URL: `http://localhost:8081/api`. Request/response format: JSON; send `Content-Type: application/json` with request bodies. No authentication.

## Common response objects
Board example: `{"id":1,"name":"Website","createdAt":"2026-10-07T13:00:00Z"}`

Task example: `{"id":2,"boardId":1,"title":"Write tests","description":null,"status":"TODO","createdAt":"2026-10-07T13:00:00Z","updatedAt":"2026-10-07T13:00:00Z"}`

Every error has the same keys: `{"error":"VALIDATION_FAILED","message":"title must not be empty","field":"title"}`. Field is null for non-field errors. Domain validation uses 400, missing resources 404, malformed bodies/parameters 400, unsupported media 415, unsupported methods 405, unexpected faults 500. Framework error codes use `HTTP_<status>`; unexpected errors use `INTERNAL_ERROR` and do not expose exception details. CORS preflight is handled by configuration.

| Method and path | Parameters / request body | Success | Example failure |
| --- | --- | --- | --- |
| GET /boards | None | 200; array of Board objects, or [] | 500: {"error":"INTERNAL_ERROR","message":"An unexpected error occurred","field":null} |
| POST /boards | {"name":"Website"} | 201; Board object | 400: {"error":"VALIDATION_FAILED","message":"name must not be empty","field":"name"} |
| GET /boards/{boardId}/tasks | Integer boardId; optional ?status=TODO, IN_PROGRESS or DONE | 200; array of Task objects, or [] for an empty existing board | 404: {"error":"NOT_FOUND","message":"Board not found","field":null} |
| POST /boards/{boardId}/tasks | Integer boardId; {"title":"Write tests","description":null} | 201; Task object, default status TODO | 400: {"error":"VALIDATION_FAILED","message":"title must not be empty","field":"title"} |
| PATCH /tasks/{taskId} | Integer taskId; {"status":"DONE"} | 200; Task object with status DONE and updatedAt refreshed | 400: {"error":"VALIDATION_FAILED","message":"status must be TODO, IN_PROGRESS or DONE","field":"status"} |
| DELETE /tasks/{taskId} | Integer taskId; no body | 204; no response body | 404: {"error":"NOT_FOUND","message":"Task not found","field":null} |
| DELETE /boards/{boardId} | Integer boardId; no body | 204; no response body; child tasks removed by database | 404: {"error":"NOT_FOUND","message":"Board not found","field":null} |

Create/update fields have to be JSON strings or null where optional. Missing/null required fields, blank text, invalid status and oversized fields return 400. A missing board on task creation returns 404; missing tasks on update/delete return 404. An invalid filter on an existing board returns 400. Status is required on PATCH; title and description editing are not supported.

Example read: `curl http://localhost:8081/api/boards` (Windows PowerShell can use `curl.exe`). For request bodies on Windows, use Postman or PowerShell `Invoke-RestMethod` to avoid shell quoting differences.

PowerShell creation example:
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/boards -ContentType application/json -Body '{"name":"Website"}'
```
## Success response examples

### GET /api/boards — 200 OK
```json
[{"id":1,"name":"Website","createdAt":"2026-10-07T13:00:00Z"}]
```

### POST /api/boards — 201 Created
```json
{"id":1,"name":"Website","createdAt":"2026-10-07T13:00:00Z"}
```

### GET /api/boards/1/tasks — 200 OK
```json
[{"id":2,"boardId":1,"title":"Write tests","description":null,"status":"TODO","createdAt":"2026-10-07T13:00:00Z","updatedAt":"2026-10-07T13:00:00Z"}]
```
An existing board with no matching tasks returns `[]`.

### POST /api/boards/1/tasks — 201 Created
```json
{"id":2,"boardId":1,"title":"Write tests","description":null,"status":"TODO","createdAt":"2026-10-07T13:00:00Z","updatedAt":"2026-10-07T13:00:00Z"}
```

### PATCH /api/tasks/2 — 200 OK
```json
{"id":2,"boardId":1,"title":"Write tests","description":null,"status":"DONE","createdAt":"2026-10-07T13:00:00Z","updatedAt":"2026-10-07T13:05:00Z"}
```

### DELETE /api/tasks/2 — 204 No Content
Empty response body.

### DELETE /api/boards/1 — 204 No Content
Empty response body. The database also deletes the board's tasks.

IDs and timestamps above are illustrative.