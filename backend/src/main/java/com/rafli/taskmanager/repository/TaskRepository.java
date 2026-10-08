package com.rafli.taskmanager.repository;

import com.rafli.taskmanager.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findByBoard(long boardId, String status);

    Optional<Task> findById(long id);

    Task create(long boardId, String title, String description);

    boolean updateStatus(long id, String status);

    boolean delete(long id);
}
