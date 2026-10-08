package com.rafli.taskmanager.repository;

import com.rafli.taskmanager.model.Task;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTaskRepository implements TaskRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Task> mapper = (rs, row) -> new Task(rs.getLong("id"), rs.getLong("board_id"),
            rs.getString("title"), rs.getString("description"), rs.getString("status"),
            rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant());

    public JdbcTaskRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Task> findByBoard(long boardId, String status) {
        if (status == null)
            return jdbc.query("SELECT * FROM tasks WHERE board_id = ? ORDER BY id", mapper, boardId);
        return jdbc.query("SELECT * FROM tasks WHERE board_id = ? AND status = ? ORDER BY id", mapper, boardId, status);
    }

    public Optional<Task> findById(long id) {
        return jdbc.query("SELECT * FROM tasks WHERE id = ?", mapper, id).stream().findFirst();
    }

    public Task create(long boardId, String title, String description) {
        var key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(
                    "INSERT INTO tasks(board_id, title, description) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, boardId);
            statement.setString(2, title);
            statement.setString(3, description);
            return statement;
        }, key);
        return findById(key.getKey().longValue()).orElseThrow();
    }

    public boolean updateStatus(long id, String status) {
        return jdbc.update("UPDATE tasks SET status = ?, updated_at = CURRENT_TIMESTAMP(6) WHERE id = ?", status,
                id) > 0;
    }

    public boolean delete(long id) {
        return jdbc.update("DELETE FROM tasks WHERE id = ?", id) > 0;
    }
}
