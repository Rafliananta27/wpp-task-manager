package com.rafli.taskmanager.service;

import com.rafli.taskmanager.model.Board;
import com.rafli.taskmanager.model.Task;
import com.rafli.taskmanager.repository.BoardRepository;
import com.rafli.taskmanager.repository.TaskRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.rafli.taskmanager.service.DomainException.Kind.*;

@Service
@Transactional
public class TaskManagerService {
    private static final Set<String> STATUSES = Set.of("TODO", "IN_PROGRESS", "DONE");
    private final BoardRepository boards;
    private final TaskRepository tasks;

    public TaskManagerService(BoardRepository boards, TaskRepository tasks) {
        this.boards = boards;
        this.tasks = tasks;
    }

    public List<Board> listBoards() {
        return boards.findAll();
    }

    public Board createBoard(String name) {
        return boards.create(requiredText(name, "name", 120));
    }

    public List<Task> listTasks(long boardId, String status) {
        requireBoard(boardId);
        if (status != null)
            validateStatus(status);
        return tasks.findByBoard(boardId, status);
    }

    public Task createTask(long boardId, String title, String description) {
        String cleanTitle = requiredText(title, "title", 200);
        if (description != null && description.length() > 10000)
            throw new DomainException(VALIDATION_FAILED, "description must be at most 10000 characters", "description");
        requireBoard(boardId);
        return tasks.create(boardId, cleanTitle, description);
    }

    public Task updateStatus(long taskId, String status) {
        validateStatus(status);
        if (!tasks.updateStatus(taskId, status))
            throw new DomainException(NOT_FOUND, "Task not found", null);
        return tasks.findById(taskId).orElseThrow(() -> new DomainException(NOT_FOUND, "Task not found", null));
    }

    public void deleteTask(long taskId) {
        if (!tasks.delete(taskId))
            throw new DomainException(NOT_FOUND, "Task not found", null);
    }

    public void deleteBoard(long boardId) {
        // MySQL's foreign key cascades the deletion; the service does not manually
        // delete tasks.
        if (!boards.delete(boardId))
            throw new DomainException(NOT_FOUND, "Board not found", null);
    }

    private void requireBoard(long id) {
        if (boards.findById(id).isEmpty())
            throw new DomainException(NOT_FOUND, "Board not found", null);
    }

    private String requiredText(String value, String field, int max) {
        if (value == null || value.isBlank())
            throw new DomainException(VALIDATION_FAILED, field + " must not be empty", field);
        String clean = value.strip();
        if (clean.length() > max)
            throw new DomainException(VALIDATION_FAILED, field + " must be at most " + max + " characters", field);
        return clean;
    }

    private void validateStatus(String status) {
        if (status == null || !STATUSES.contains(status))
            throw new DomainException(VALIDATION_FAILED, "status must be TODO, IN_PROGRESS or DONE", "status");
    }
}
