package org.emotion.detect.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for Dashboard-related database operations
 * Handles queries for keyword statistics and analytics
 */
public interface DashboardRepository {
    
    /**
     * Get keyword statistics for a specific time period
     * @param startDate start date for the time period
     * @param endDate end date for the time period
     * @return list of keyword counts as map entries
     */
    List<Map<String, Object>> getKeywordStatsByTimePeriod(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get all keywords within a time period
     * @param startDate start date for the time period
     * @param endDate end date for the time period
     * @return list of keywords with their counts
     */
    List<Map<String, Object>> getAllKeywordsByTimePeriod(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get all sentiment chart data aggregated by date
     * @return list of sentiment data grouped by date
     */
    List<Map<String, Object>> getAllSentimentChartData();
    
    /**
     * Insert emotion keyword into daily_keywords table
     * @param emotion the emotion keyword to insert
     * @return the generated ID of the inserted record
     */
    int insertEmotionKeyword(String emotion);
    
    /**
     * Check if sentiment record exists for a specific date
     * @param date the date to check
     * @return true if record exists, false otherwise
     */
    boolean existsSentimentRecordForDate(java.sql.Date date);
    
    /**
     * Insert new sentiment record for a date
     * @param date the date
     * @param positive positive count (0 or 1)
     * @param negative negative count (0 or 1)
     * @param neutral neutral count (0 or 1)
     */
    void insertSentimentRecord(java.sql.Date date, int positive, int negative, int neutral);
    
    /**
     * Update sentiment counts for existing date
     * @param date the date
     * @param positive positive count increment
     * @param negative negative count increment
     * @param neutral neutral count increment
     */
    void updateSentimentCounts(java.sql.Date date, int positive, int negative, int neutral);
}
