package org.emotion.detect.repository.impl;

import org.emotion.detect.entity.EmotionalLevel;
import org.emotion.detect.repository.EmotionalLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of EmotionalLevelRepository
 * Handles database operations for emotional level data
 */
@Repository
public class EmotionalLevelRepositoryImpl implements EmotionalLevelRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<EmotionalLevel> emotionalLevelRowMapper = new RowMapper<EmotionalLevel>() {
        @Override
        public EmotionalLevel mapRow(ResultSet rs, int rowNum) throws SQLException {
            EmotionalLevel level = new EmotionalLevel();
            level.setLevel(rs.getString("level"));
            level.setTestFeedback(rs.getString("test_feedback"));
            level.setTips(rs.getString("tips"));
            level.setRefs(rs.getString("refs"));
            level.setGrowthTips(rs.getString("growth_tips"));
            return level;
        }
    };

    @Override
    public EmotionalLevel findByLevelText(String levelText) {
        String sql = "SELECT level, test_feedback, tips, refs, growth_tips FROM emotional_levels WHERE level = ?";
        System.out.println("Executing SQL: " + sql + " with levelText: " + levelText);
        try {
            EmotionalLevel result = jdbcTemplate.queryForObject(sql, emotionalLevelRowMapper, levelText);
            System.out.println("Query result: " + (result != null ? "Found" : "Not found"));
            return result;
        } catch (Exception e) {
            System.out.println("Error executing query: " + e.getMessage());
            return null;
        }
    }
}
