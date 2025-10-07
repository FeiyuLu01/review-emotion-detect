package org.emotion.detect.repository.impl;

import org.emotion.detect.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Implementation of DashboardRepository
 * Handles database operations for dashboard analytics
 */
@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getKeywordStatsByTimePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT keyword, COUNT(*) as count " +
                    "FROM daily_keywords " +
                    "WHERE record_date >= ? AND record_date <= ? " +
                    "GROUP BY keyword " +
                    "ORDER BY count DESC";
        
        System.out.println("Executing keyword stats SQL: " + sql);
        System.out.println("Start date: " + startDate + ", End date: " + endDate);
        
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, startDate, endDate);
        System.out.println("Query result size: " + result.size());
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getAllKeywordsByTimePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT keyword, COUNT(*) as count " +
                    "FROM daily_keywords " +
                    "WHERE record_date >= ? AND record_date <= ? " +
                    "GROUP BY keyword " +
                    "ORDER BY count DESC";
        
        System.out.println("Executing all keywords SQL: " + sql);
        System.out.println("Start date: " + startDate + ", End date: " + endDate);
        
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, startDate, endDate);
        System.out.println("Query result size: " + result.size());
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getAllSentimentChartData() {
        String sql = "SELECT DATE(record_date) as date, " +
                    "SUM(positive) as positive, " +
                    "SUM(negative) as negative, " +
                    "SUM(neutral) as neutral " +
                    "FROM sentiment_summary " +
                    "GROUP BY DATE(record_date) " +
                    "ORDER BY DATE(record_date) ASC";
        
        System.out.println("Executing all sentiment chart SQL: " + sql);
        
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        System.out.println("All sentiment chart query result size: " + result.size());
        
        return result;
    }
}
