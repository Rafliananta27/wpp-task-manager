package com.rafli.taskmanager.repository;

import com.rafli.taskmanager.model.Board;
import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    List<Board> findAll();

    Optional<Board> findById(long id);

    Board create(String name);

    boolean delete(long id);
}
