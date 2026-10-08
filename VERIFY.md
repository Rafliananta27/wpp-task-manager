# Verification before submission
Recorded results are in the root README. This checklist includes additional checks that are not all complete; do not interpret it as a full pass report.

1. In backend: run `mvn test`; record the summary. Run `mvn package` to compile the application.
2. In frontend: run `npm install` then `npm run build`; preserve package-lock.json.
3. Start MySQL and backend from their README. Confirm Flyway creates tables from an empty database.
4. Create two boards. Create several tasks with and without descriptions. Confirm new status is TODO.
5. Test all three status values and filter combinations. Confirm filtering returns only matching tasks and empty board gives [].
6. Check missing/empty/whitespace titles and names, long fields, malformed JSON, invalid status and nonexistent IDs. Use Postman; confirm documented status codes and the same three error keys.
7. Delete a task; response must be 204 with no body. Repeat; response must be 404.
8. Delete a board with tasks; inspect MySQL to confirm its tasks are gone and another board's tasks remain.
9. Attempt a direct SQL task insert with an absent board_id: MySQL must reject it. Attempt an invalid status: CHECK constraint must reject it. Inspect `SHOW CREATE TABLE tasks`.
10. Create a task and restart only the backend; the task must persist.
11. Stop frontend; leave backend running. `curl http://localhost:8081/api/boards` must still work.
12. Stop backend; start frontend. The page must load with a useful error and Retry button. Restore backend and retry.
13. In browser, create/select/delete boards; create/change/delete/filter tasks. Confirm no full reload, no blank page and errors are visible. Try slow/offline network mode for loading and failures.
14. Follow both READMEs on a clean checkout. Ensure no secrets, node_modules, target, dist or real .env files are included in the submission.

Mocked service tests prove service behaviour, not real database cascade. Step 8 and 9 are essential MySQL integration checks.
