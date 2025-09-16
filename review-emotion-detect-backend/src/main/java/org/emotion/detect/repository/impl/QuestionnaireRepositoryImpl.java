package org.emotion.detect.repository.impl;

import org.emotion.detect.entity.LabeledComment;
import org.emotion.detect.repository.QuestionnaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of QuestionnaireRepository
 * Handles database operations for questionnaire generation
 */
@Repository
public class QuestionnaireRepositoryImpl implements QuestionnaireRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<LabeledComment> labeledCommentRowMapper = new RowMapper<LabeledComment>() {
        @Override
        public LabeledComment mapRow(ResultSet rs, int rowNum) throws SQLException {
            LabeledComment comment = new LabeledComment();
            comment.setId(rs.getInt("id"));
            comment.setTextNatural(rs.getString("text_natural"));
            comment.setFineLabels(rs.getString("fine_labels"));
            comment.setEkmanLabels(rs.getString("ekman_labels"));
            return comment;
        }
    };

    @Override
    public List<LabeledComment> findRandomLabeledComments(int limit) {
        // Use labeled_comments table with correct field names
        String sql = "SELECT id, text_natural, fine_labels, ekman_labels FROM labeled_comments ORDER BY RAND() LIMIT ?";
        System.out.println("Executing SQL: " + sql + " with limit: " + limit);
        try {
            List<LabeledComment> result = jdbcTemplate.query(sql, labeledCommentRowMapper, limit);
            System.out.println("Query result size: " + result.size());
            return result;
        } catch (Exception e) {
            System.out.println("Error executing query: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
}
