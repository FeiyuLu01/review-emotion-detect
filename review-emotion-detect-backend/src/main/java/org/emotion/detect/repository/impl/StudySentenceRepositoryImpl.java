package org.emotion.detect.repository.impl;

import org.emotion.detect.entity.StudySentence;
import org.emotion.detect.repository.StudySentenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StudySentenceRepositoryImpl implements StudySentenceRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<StudySentence> studySentenceRowMapper = new RowMapper<StudySentence>() {
        @Override
        public StudySentence mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudySentence sentence = new StudySentence();
            sentence.setSentenceId(rs.getInt("sentence_id"));
            sentence.setSection(rs.getString("section"));
            sentence.setSentenceText(rs.getString("sentence_text"));
            sentence.setGoemotionLabel(rs.getString("goemotion_label"));
            return sentence;
        }
    };

    @Override
    public List<String> findDistinctSentenceTextBySection(String section) {
        String sql = "SELECT DISTINCT sentence_text FROM study_sentence WHERE section = ?";
        return jdbcTemplate.queryForList(sql, String.class, section);
    }
}
