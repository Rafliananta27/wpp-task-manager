const baseUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api').replace(/\/$/, '');
async function request(path, options = {}) {
    let response;
    try {
        response = await fetch(`${baseUrl}${path}`, {
            ...options, headers: { 'Content-Type': 'application/json', ...options.headers },
        });
    } catch (error) {
        if (error.name === 'AbortError') throw error;
        throw new Error('Cannot reach the backend. Check that it is running, then retry.');
    }
    if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        throw new Error(body.message || `Request failed (${response.status})`);
    }
    return response.status === 204 ? null : response.json();
}
export const api = {
    boards: (signal) => request('/boards', { signal }),
    createBoard: (name) => request('/boards', { method: 'POST', body: JSON.stringify({ name }) }),
    deleteBoard: (id) => request(`/boards/${id}`, { method: 'DELETE' }),
    tasks: (boardId, status, signal) => request(`/boards/${boardId}/tasks${status ? `?status=${encodeURIComponent(status)}` : ''}`, { signal }),
    createTask: (id, title, description) => request(`/boards/${id}/tasks`, { method: 'POST', body: JSON.stringify({ title, description }) }),
    updateStatus: (id, status) => request(`/tasks/${id}`, { method: 'PATCH', body: JSON.stringify({ status }) }),
    deleteTask: (id) => request(`/tasks/${id}`, { method: 'DELETE' }),
};
