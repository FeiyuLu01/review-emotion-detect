package org.emotion.detect.repository.impl;

import org.emotion.detect.entity.Post;
import org.emotion.detect.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of PostRepository
 * Handles database operations for anonymous posts
 */
@Repository
public class PostRepositoryImpl implements PostRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Post> postRowMapper = new RowMapper<Post>() {
        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            Post post = new Post();
            post.setPostId(rs.getInt("post_id"));
            post.setContent(rs.getString("content"));
            post.setBgColor(rs.getString("bg_color"));
            post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return post;
        }
    };

    @Override
    public Post save(Post post) {
        String sql = "INSERT INTO anonymous_posts (content, bg_color, created_at) VALUES (?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getContent());
            ps.setString(2, post.getBgColor());
            ps.setObject(3, LocalDateTime.now());
            return ps;
        }, keyHolder);
        
        // Set the generated ID
        post.setPostId(keyHolder.getKey().intValue());
        post.setCreatedAt(LocalDateTime.now());
        
        return post;
    }

    @Override
    public List<Post> findAll() {
        String sql = "SELECT post_id, content, bg_color, created_at FROM anonymous_posts ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, postRowMapper);
    }
}
