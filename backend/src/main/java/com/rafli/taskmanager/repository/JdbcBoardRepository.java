package com.rafli.taskmanager.repository;

import com.rafli.taskmanager.model.Board;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcBoardRepository implements BoardRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Board> mapper = (rs, row) -> new Board(rs.getLong("id"), rs.getString("name"),
            rs.getTimestamp("created_at").toInstant());

    public JdbcBoardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Board> findAll() {
        return jdbc.query("SELECT * FROM boards ORDER BY id", mapper);
    }

    public Optional<Board> findById(long id) {
        return jdbc.query("SELECT * FROM boards WHERE id = ?", mapper, id).stream().findFirst();
    }

    public Board create(String name) {
        var key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement("INSERT INTO boards(name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            return statement;
        }, key);
        return findById(key.getKey().longValue()).orElseThrow();
    }

    public boolean delete(long id) {
        return jdbc.update("DELETE FROM boards WHERE id = ?", id) > 0;
    }
}
