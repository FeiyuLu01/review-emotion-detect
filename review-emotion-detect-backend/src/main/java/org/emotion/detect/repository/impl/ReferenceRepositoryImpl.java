package org.emotion.detect.repository.impl;

import org.emotion.detect.entity.ReferenceItem;
import org.emotion.detect.repository.ReferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ReferenceRepositoryImpl implements ReferenceRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<ReferenceItem> referenceItemRowMapper = new RowMapper<ReferenceItem>() {
        @Override
        public ReferenceItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            ReferenceItem item = new ReferenceItem();
            item.setRefId(rs.getInt("ref_id"));
            item.setTitle(rs.getString("title"));
            item.setDoi(rs.getString("doi"));
            item.setUrl(rs.getString("url"));
            item.setCitation(rs.getString("citation"));
            return item;
        }
    };

    @Override
    public ReferenceItem findFirstReferenceBySection(String section) {
        String sql = "SELECT DISTINCT ri.ref_id, ri.title, ri.doi, ri.url, ri.citation " +
                    "FROM study_sentence ss " +
                    "JOIN sentence_reference sr ON ss.sentence_id = sr.sentence_id " +
                    "JOIN reference_item ri ON sr.ref_id = ri.ref_id " +
                    "WHERE ss.section = ? " +
                    "ORDER BY ri.ref_id " +
                    "LIMIT 1";
        List<ReferenceItem> results = jdbcTemplate.query(sql, referenceItemRowMapper, section);
        return results.isEmpty() ? null : results.get(0);
    }
}
