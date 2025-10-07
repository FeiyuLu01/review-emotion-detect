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
}
