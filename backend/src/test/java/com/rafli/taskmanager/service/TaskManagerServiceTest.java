package com.rafli.taskmanager.service;

import com.rafli.taskmanager.model.*;
import com.rafli.taskmanager.repository.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskManagerServiceTest {
    BoardRepository boards;
    TaskRepository tasks;
    TaskManagerService service;
    final Instant now = Instant.parse("2026-10-07T00:00:00Z");

    @BeforeEach
    void setup() {
        boards = mock(BoardRepository.class);
        tasks = mock(TaskRepository.class);
        service = new TaskManagerService(boards, tasks);
    }

    void boardExists() {
        when(boards.findById(1)).thenReturn(Optional.of(new Board(1, "Project", now)));
    }

    @Test
    void createsTaskOnExistingBoardAndTrimsTitle() {
        boardExists();
        Task expected = new Task(2, 1, "Write tests", "Service tests", "TODO", now, now);
        when(tasks.create(1, "Write tests", "Service tests")).thenReturn(expected);
        assertEquals(expected, service.createTask(1, " Write tests ", "Service tests"));
        verify(tasks).create(1, "Write tests", "Service tests");
    }

    @Test
    void filtersByStatus() {
        boardExists();
        Task done = new Task(2, 1, "Finished", null, "DONE", now, now);
        when(tasks.findByBoard(1, "DONE")).thenReturn(List.of(done));
        assertEquals(List.of(done), service.listTasks(1, "DONE"));
        verify(tasks).findByBoard(1, "DONE");
    }

    @Test
    void emptyBoardReturnsEmptyList() {
        boardExists();
        when(tasks.findByBoard(1, null)).thenReturn(List.of());
        assertTrue(service.listTasks(1, null).isEmpty());
    }

    @Test
    void deletionDelegatesToDatabaseCascade() {
        when(boards.delete(1)).thenReturn(true);
        assertDoesNotThrow(() -> service.deleteBoard(1));
        verify(boards).delete(1);
        verifyNoInteractions(tasks);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t\n" })
    void rejectsBlankTaskTitle(String title) {
        DomainException ex = assertThrows(DomainException.class, () -> service.createTask(1, title, null));
        assertEquals(DomainException.Kind.VALIDATION_FAILED, ex.kind());
        assertEquals("title", ex.field());
        verifyNoInteractions(tasks);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    void rejectsBlankBoardName(String name) {
        DomainException ex = assertThrows(DomainException.class, () -> service.createBoard(name));
        assertEquals("name", ex.field());
        verifyNoInteractions(boards);
    }

    @Test
    void rejectsLongBoardName() {
        assertThrows(DomainException.class, () -> service.createBoard("a".repeat(121)));
        verifyNoInteractions(boards);
    }

    @Test
    void rejectsLongTitle() {
        assertThrows(DomainException.class, () -> service.createTask(1, "a".repeat(201), null));
        verifyNoInteractions(tasks);
    }

    @Test
    void rejectsLongDescription() {
        assertThrows(DomainException.class, () -> service.createTask(1, "Title", "a".repeat(10001)));
        verifyNoInteractions(tasks);
    }

    @Test
    void missingBoardCannotReceiveTask() {
        when(boards.findById(99)).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> service.createTask(99, "Title", null));
        assertEquals(DomainException.Kind.NOT_FOUND, ex.kind());
        verifyNoInteractions(tasks);
    }

    @Test
    void missingBoardCannotListTasks() {
        when(boards.findById(99)).thenReturn(Optional.empty());
        assertEquals(DomainException.Kind.NOT_FOUND,
                assertThrows(DomainException.class, () -> service.listTasks(99, null)).kind());
        verifyNoInteractions(tasks);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "INVALID", "done", " " })
    void rejectsInvalidUpdateStatus(String status) {
        assertEquals("status", assertThrows(DomainException.class, () -> service.updateStatus(2, status)).field());
        verifyNoInteractions(tasks);
    }

    @Test
    void rejectsInvalidFilter() {
        boardExists();
        assertThrows(DomainException.class, () -> service.listTasks(1, "INVALID"));
        verifyNoInteractions(tasks);
    }

    @Test
    void missingTaskCannotBeUpdated() {
        when(tasks.updateStatus(99, "DONE")).thenReturn(false);
        assertEquals(DomainException.Kind.NOT_FOUND,
                assertThrows(DomainException.class, () -> service.updateStatus(99, "DONE")).kind());
    }

    @Test
    void missingTaskCannotBeDeleted() {
        when(tasks.delete(99)).thenReturn(false);
        assertEquals(DomainException.Kind.NOT_FOUND,
                assertThrows(DomainException.class, () -> service.deleteTask(99)).kind());
    }

    @Test
    void missingBoardCannotBeDeleted() {
        when(boards.delete(99)).thenReturn(false);
        assertEquals(DomainException.Kind.NOT_FOUND,
                assertThrows(DomainException.class, () -> service.deleteBoard(99)).kind());
    }

    @Test
    void createsBoardWithTrimmedName() {
        Board expected = new Board(1, "Project", now);
        when(boards.create("Project")).thenReturn(expected);
        assertEquals(expected, service.createBoard(" Project "));
    }

    @Test
    void updatesStatusAndReturnsSavedTask() {
        Task expected = new Task(2, 1, "Title", null, "DONE", now, now);
        when(tasks.updateStatus(2, "DONE")).thenReturn(true);
        when(tasks.findById(2)).thenReturn(Optional.of(expected));
        assertEquals(expected, service.updateStatus(2, "DONE"));
    }

    @Test
    void deletesExistingTask() {
        when(tasks.delete(2)).thenReturn(true);
        service.deleteTask(2);
        verify(tasks).delete(2);
    }
}
