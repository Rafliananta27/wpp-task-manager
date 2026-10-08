import React from 'react';
export const statuses = ['TODO', 'IN_PROGRESS', 'DONE'];
export const label = (status) => ({ TODO: 'To Do', IN_PROGRESS: 'In Progress', DONE: 'Done' })[status];
export default function TaskList({ tasks, busy, onStatus, onDelete }) {
    return <ul className="tasks">{tasks.map(task => <li key={task.id}>
        <h3>{task.title}</h3><p className="description">{task.description || 'No description'}</p>
        <small>Created {new Date(task.createdAt).toLocaleString()}</small>
        <div className="actions"><label>Status <select value={task.status} disabled={busy} onChange={e => onStatus(task.id, e.target.value)}>
            {statuses.map(status => <option key={status} value={status}>{label(status)}</option>)}
        </select></label><button disabled={busy} onClick={() => onDelete(task.id)}>Delete task</button></div>
    </li>)}</ul>;
}
