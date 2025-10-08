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

    @Override
    public int insertEmotionKeyword(String emotion) {
        String sql = "INSERT INTO daily_keywords (keyword, record_date) VALUES (?, NOW())";
        
        System.out.println("Inserting emotion keyword: " + emotion);
        
        return jdbcTemplate.update(sql, emotion);
    }

    @Override
    public boolean existsSentimentRecordForDate(java.sql.Date date) {
        String sql = "SELECT COUNT(*) FROM sentiment_summary WHERE DATE(record_date) = ?";
        
        System.out.println("Checking if sentiment record exists for date: " + date);
        
        int count = jdbcTemplate.queryForObject(sql, Integer.class, date);
        boolean exists = count > 0;
        
        System.out.println("Sentiment record exists for date " + date + ": " + exists);
        
        return exists;
    }

    @Override
    public void insertSentimentRecord(java.sql.Date date, int positive, int negative, int neutral) {
        String sql = "INSERT INTO sentiment_summary (record_date, positive, negative, neutral) VALUES (?, ?, ?, ?)";
        
        System.out.println("Inserting new sentiment record for date: " + date + 
                          " positive: " + positive + " negative: " + negative + " neutral: " + neutral);
        
        jdbcTemplate.update(sql, date, positive, negative, neutral);
    }

    @Override
    public void updateSentimentCounts(java.sql.Date date, int positive, int negative, int neutral) {
        String sql = "UPDATE sentiment_summary SET positive = positive + ?, negative = negative + ?, neutral = neutral + ? WHERE DATE(record_date) = ?";
        
        System.out.println("Updating sentiment counts for date: " + date + 
                          " positive: +" + positive + " negative: +" + negative + " neutral: +" + neutral);
        
        jdbcTemplate.update(sql, positive, negative, neutral, date);
    }
}
