import React, { useEffect, useState } from 'react';
import { api } from './api';
import TaskList, { statuses, label } from './TaskList';
export default function App() {
    const [boards, setBoards] = useState([]);
    const [selected, setSelected] = useState('');
    const [tasks, setTasks] = useState([]);
    const [filter, setFilter] = useState('');
    const [boardName, setBoardName] = useState('');
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [boardLoading, setBoardLoading] = useState(true);
    const [taskLoading, setTaskLoading] = useState(false);
    const [boardError, setBoardError] = useState('');
    const [taskError, setTaskError] = useState('');
    const [actionError, setActionError] = useState('');
    const [busy, setBusy] = useState(false);
    const [retry, setRetry] = useState(0);
    useEffect(() => {
        const controller = new AbortController();
        setBoardLoading(true); setBoardError('');
        api.boards(controller.signal).then(data => {
            setBoards(data);
            setSelected(previous => data.some(b => String(b.id) === previous) ? previous : data.length ? String(data[0].id) : '');
        }).catch(e => { if (e.name !== 'AbortError') setBoardError(e.message); })
            .finally(() => { if (!controller.signal.aborted) setBoardLoading(false); });
        return () => controller.abort();
    }, [retry]);
    useEffect(() => {
        if (!selected) { setTasks([]); setTaskLoading(false); setTaskError(''); return; }
        const controller = new AbortController();
        setTasks([]); setTaskLoading(true); setTaskError('');
        api.tasks(selected, filter, controller.signal).then(setTasks)
            .catch(e => { if (e.name !== 'AbortError') setTaskError(e.message); })
            .finally(() => { if (!controller.signal.aborted) setTaskLoading(false); });
        return () => controller.abort();
    }, [selected, filter, retry]);
    async function mutate(action) {
        setActionError(''); setBusy(true);
        try { await action(); } catch (e) { setActionError(e.message); } finally { setBusy(false); }
    }
    function addBoard(e) {
        e.preventDefault();
        if (!boardName.trim()) { setActionError('Board name must not be empty.'); return; }
        mutate(async () => { const board = await api.createBoard(boardName.trim()); setBoards(old => [...old, board]); setSelected(String(board.id)); setFilter(''); setBoardName(''); });
    }
    function addTask(e) {
        e.preventDefault();
        if (!title.trim()) { setActionError('Task title must not be empty.'); return; }
        mutate(async () => { await api.createTask(selected, title.trim(), description || null); setTitle(''); setDescription(''); setRetry(old => old + 1); });
    }
    function removeBoard() {
        if (!window.confirm('Delete this board and all its tasks?')) return;
        mutate(async () => { await api.deleteBoard(selected); setSelected(''); setRetry(old => old + 1); });
    }
    return <main><header><h1>Task Manager</h1><p>Board for organize team's work</p></header>
        {actionError && <p role="alert" className="error">{actionError}</p>}
        <section><h2>Boards</h2>
            <form onSubmit={addBoard}><label>New board name <input value={boardName} maxLength={120} onChange={e => setBoardName(e.target.value)} required /></label><button disabled={busy}>Create board</button></form>
            {boardLoading && <p role="status">Loading boards…</p>}
            {boardError && <p role="alert" className="error">{boardError} <button disabled={busy} onClick={() => setRetry(old => old + 1)}>Retry</button></p>}
            {!boardLoading && !boardError && boards.length === 0 && <p>No boards yet. Create your first board above.</p>}
            {boards.length > 0 && <div className="actions"><label>Selected board <select value={selected} disabled={busy || boardLoading} onChange={e => { setSelected(e.target.value); setTitle(''); setDescription(''); setActionError(''); }}>
                {!selected && <option value="">Select a board</option>}{boards.map(board => <option key={board.id} value={board.id}>{board.name}</option>)}
            </select></label><button disabled={busy || !selected || boardLoading} onClick={removeBoard}>Delete board</button></div>}
        </section>
        {selected && <section><h2>Tasks</h2>
            <form onSubmit={addTask}><label>Title <input value={title} maxLength={200} required onChange={e => setTitle(e.target.value)} /></label><label>Description (optional)<textarea maxLength={10000} value={description} onChange={e => setDescription(e.target.value)} /></label><button disabled={busy || taskLoading}>Create task</button></form>
            <label>Filter by status <select value={filter} disabled={busy} onChange={e => setFilter(e.target.value)}><option value="">All statuses</option>{statuses.map(status => <option key={status} value={status}>{label(status)}</option>)}</select></label>
            {taskLoading && <p role="status">Loading tasks…</p>}
            {taskError && <p role="alert" className="error">{taskError} <button disabled={busy} onClick={() => setRetry(old => old + 1)}>Retry</button></p>}
            {!taskLoading && !taskError && tasks.length === 0 && <p>{filter ? 'No tasks match this status.' : 'No tasks yet. Create your first task above.'}</p>}
            {!taskLoading && !taskError && <TaskList tasks={tasks} busy={busy} onStatus={(id, status) => mutate(async () => { const updated = await api.updateStatus(id, status); setTasks(old => old.map(t => t.id === id ? updated : t).filter(t => !filter || t.status === filter)); })} onDelete={id => mutate(async () => { await api.deleteTask(id); setTasks(old => old.filter(t => t.id !== id)); })} />}
        </section>}
    </main>;
}
