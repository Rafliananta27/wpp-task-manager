# Frontend
## Prerequisites
Node.js 20+ and npm; internet access for the first dependency installation.

## Setup
From this directory:
1. Copy `.env.example` to `.env` (PowerShell: `Copy-Item .env.example .env`; macOS/Linux: `cp .env.example .env`).
2. Run `npm ci`.
3. Run `npm run dev -- --port 5173 --strictPort`.
4. Open `http://localhost:5173`.

The frontend starts without the backend. It displays a connection error and Retry button when the API cannot be reached. Its default API URL is `http://localhost:8081/api`; change `VITE_API_BASE_URL` in `.env` and restart Vite to use a different backend. For a different frontend origin, update the backend's FRONTEND_ORIGIN too.

Build: `npm run build`. Preview: `npm run preview -- --port 5173 --strictPort`. Each build produces only the frontend's `dist` directory. The backend has a separate manifest and command. No backend code is imported. API calls are centralised in `src/api.js`; task rendering is in `TaskList.jsx`; `App.jsx` coordinates forms and state. React escapes user-provided text.

## Behaviour
Select/create/delete a board; create tasks; change status without reload; delete tasks; filter tasks by status. Browser validation and API validation errors are displayed. Loading, empty and unavailable-backend states are explicit. Mutations disable conflicting controls; abort controllers cancel stale list requests. Board deletion asks for confirmation because it also deletes tasks.

## Trade-offs and verification
Simple CSS only. No frontend tests; the brief makes them optional. Network workflows should be checked manually using the root checklist. The production build passed on the candidate's machine. package-lock.json is included for reproducible installation. On Windows PowerShell, use npm.cmd instead of npm if execution policy blocks npm.ps1.
