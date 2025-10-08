package org.emotion.detect.service.impl;

import org.emotion.detect.dto.KeywordStatsResponse;
import org.emotion.detect.dto.SentimentChartResponse;
import org.emotion.detect.enums.SentimentType;
import org.emotion.detect.repository.DashboardRepository;
import org.emotion.detect.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of DashboardService
 * Handles business logic for dashboard analytics
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Override
    public KeywordStatsResponse getKeywordStats(String timePeriod) {
        // Calculate date range based on time period
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(endDate, timePeriod);
        
        System.out.println("Calculated date range: " + startDate + " to " + endDate);
        
        // Get all keyword statistics from repository for the time period
        List<Map<String, Object>> keywordStats = dashboardRepository.getAllKeywordsByTimePeriod(startDate, endDate);
        
        // Convert to sorted map
        Map<String, Integer> sortedStats = new LinkedHashMap<>();
        for (Map<String, Object> stat : keywordStats) {
            String keyword = (String) stat.get("keyword");
            Integer count = ((Number) stat.get("count")).intValue();
            sortedStats.put(keyword, count);
        }
        
        return new KeywordStatsResponse(sortedStats, timePeriod, keywordStats.size());
    }

    @Override
    public SentimentChartResponse getSentimentChartData() {
        System.out.println("Getting all sentiment chart data for trend analysis");
        
        // Get all sentiment chart data from repository (no date filtering)
        List<Map<String, Object>> sentimentData = dashboardRepository.getAllSentimentChartData();
        
        // Format data for ECharts
        List<String> dates = new ArrayList<>();
        List<Integer> positive = new ArrayList<>();
        List<Integer> negative = new ArrayList<>();
        List<Integer> neutral = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M.d");
        
        for (Map<String, Object> data : sentimentData) {
            // Format date for ECharts (e.g., "9.10", "9.11")
            java.sql.Date sqlDate = (java.sql.Date) data.get("date");
            LocalDateTime dateTime = sqlDate.toLocalDate().atStartOfDay();
            String formattedDate = dateTime.format(formatter);
            dates.add(formattedDate);
            
            // Get sentiment counts
            positive.add(((Number) data.get("positive")).intValue());
            negative.add(((Number) data.get("negative")).intValue());
            neutral.add(((Number) data.get("neutral")).intValue());
        }
        
        return new SentimentChartResponse(dates, negative, positive, neutral);
    }
    
    @Override
    public boolean processEmotion(String emotion) {
        try {
            System.out.println("Processing emotion: " + emotion);
            
            // 1. Insert emotion into daily_keywords table
            dashboardRepository.insertEmotionKeyword(emotion);
            System.out.println("Successfully inserted emotion into daily_keywords: " + emotion);
            
            // 2. Determine sentiment type using enum
            String sentimentType = SentimentType.getSentimentTypeString(emotion);
            System.out.println("Determined sentiment type for '" + emotion + "': " + sentimentType);
            
            // 3. Get current date
            LocalDate currentDate = LocalDate.now();
            java.sql.Date sqlDate = java.sql.Date.valueOf(currentDate);
            
            // 4. Check if sentiment record exists for today
            boolean recordExists = dashboardRepository.existsSentimentRecordForDate(sqlDate);
            
            // 5. Set sentiment counts based on type
            int positive = 0, negative = 0, neutral = 0;
            switch (sentimentType.toLowerCase()) {
                case "positive":
                    positive = 1;
                    break;
                case "negative":
                    negative = 1;
                    break;
                case "neutral":
                    neutral = 1;
                    break;
            }
            
            // 6. Insert or update sentiment record
            if (recordExists) {
                dashboardRepository.updateSentimentCounts(sqlDate, positive, negative, neutral);
                System.out.println("Updated existing sentiment record for date: " + sqlDate);
            } else {
                dashboardRepository.insertSentimentRecord(sqlDate, positive, negative, neutral);
                System.out.println("Inserted new sentiment record for date: " + sqlDate);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error processing emotion: " + emotion + ", Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * Calculate start date based on time period
     * @param endDate the end date (usually now)
     * @param timePeriod the time period (weekly, monthly, yearly)
     * @return the calculated start date
     */
    private LocalDateTime calculateStartDate(LocalDateTime endDate, String timePeriod) {
        switch (timePeriod.toLowerCase()) {
            case "weekly":
                return endDate.minus(7, ChronoUnit.DAYS);
            case "monthly":
                return endDate.minus(1, ChronoUnit.MONTHS);
            case "yearly":
                return endDate.minus(1, ChronoUnit.YEARS);
            default:
                throw new IllegalArgumentException("Invalid time period: " + timePeriod);
        }
    }
}
