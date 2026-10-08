package com.rafli.taskmanager.web;

import com.rafli.taskmanager.model.Board;
import com.rafli.taskmanager.model.Task;
import com.rafli.taskmanager.service.TaskManagerService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TaskManagerController {
    private final TaskManagerService service;

    public TaskManagerController(TaskManagerService service) {
        this.service = service;
    }

    public record CreateBoard(String name) {
    }

    public record CreateTask(String title, String description) {
    }

    public record UpdateStatus(String status) {
    }

    @GetMapping("/boards")
    public List<Board> boards() {
        return service.listBoards();
    }

    @PostMapping("/boards")
    public ResponseEntity<Board> createBoard(@RequestBody CreateBoard request) {
        return ResponseEntity.status(201).body(service.createBoard(request.name()));
    }

    @GetMapping("/boards/{boardId}/tasks")
    public List<Task> tasks(@PathVariable long boardId, @RequestParam(required = false) String status) {
        return service.listTasks(boardId, status);
    }

    @PostMapping("/boards/{boardId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable long boardId, @RequestBody CreateTask request) {
        return ResponseEntity.status(201).body(service.createTask(boardId, request.title(), request.description()));
    }

    @PatchMapping("/tasks/{taskId}")
    public Task update(@PathVariable long taskId, @RequestBody UpdateStatus request) {
        return service.updateStatus(taskId, request.status());
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable long taskId) {
        service.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable long boardId) {
        service.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
}
