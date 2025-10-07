package org.emotion.detect.service;

import org.emotion.detect.dto.KeywordStatsResponse;
import org.emotion.detect.dto.SentimentChartResponse;

/**
 * Service interface for Dashboard operations
 * Handles business logic for analytics and statistics
 */
public interface DashboardService {
    
    /**
     * Get keyword statistics for a specific time period
     * @param timePeriod the time period for analysis (weekly, monthly, yearly)
     * @return keyword statistics response with sorted counts
     */
    KeywordStatsResponse getKeywordStats(String timePeriod);
    
    /**
     * Get sentiment chart data for ECharts visualization
     * Returns all sentiment data for trend analysis
     * @return sentiment chart response with date and sentiment counts
     */
    SentimentChartResponse getSentimentChartData();
}
