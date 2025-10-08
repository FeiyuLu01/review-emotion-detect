package org.emotion.detect.controller;

import org.emotion.detect.dto.EmotionRequest;
import org.emotion.detect.dto.KeywordStatsResponse;
import org.emotion.detect.dto.SentimentChartResponse;
import org.emotion.detect.service.DashboardService;
import org.emotion.detect.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * REST controller for handling dashboard analytics requests
 * Provides endpoints for keyword statistics and analytics
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;

    /**
     * Get keyword statistics for a specific time period
     * @param timePeriod the time period for analysis (weekly, monthly, yearly)
     * @return response containing keyword statistics sorted by count
     */
    @GetMapping("/sort-emotion")
    public ResponseVo<KeywordStatsResponse> getKeywordStats(
            @RequestParam("timePeriod") 
            @Pattern(regexp = "^(weekly|monthly|yearly)$", message = "Time period must be one of: weekly, monthly, yearly")
            String timePeriod) {
        try {
            KeywordStatsResponse response = dashboardService.getKeywordStats(timePeriod);
            return ResponseVo.success(response);
        } catch (IllegalArgumentException e) {
            return ResponseVo.error(org.emotion.detect.enums.ResponseEnum.ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseVo.error(org.emotion.detect.enums.ResponseEnum.ERROR, "Error getting keyword statistics: " + e.getMessage());
        }
    }

    /**
     * Get sentiment chart data for ECharts visualization
     * Returns all sentiment data for trend analysis
     * @return response containing sentiment chart data formatted for ECharts
     */
    @GetMapping("/line-chart")
    public ResponseVo<SentimentChartResponse> getSentimentChartData() {
        try {
            SentimentChartResponse response = dashboardService.getSentimentChartData();
            return ResponseVo.success(response);
        } catch (Exception e) {
            return ResponseVo.error(org.emotion.detect.enums.ResponseEnum.ERROR, "Error getting sentiment chart data: " + e.getMessage());
        }
    }

    /**
     * Process emotion and update both daily_keywords and sentiment_summary tables
     * @param emotionRequest the emotion request containing the emotion to process
     * @return response indicating success or failure
     */
    @PostMapping("/process-emotion")
    public ResponseVo<String> processEmotion(@Valid @RequestBody EmotionRequest emotionRequest) {
        try {
            boolean success = dashboardService.processEmotion(emotionRequest.getEmotion());
            if (success) {
                return ResponseVo.success("Emotion processed successfully: " + emotionRequest.getEmotion());
            } else {
                return ResponseVo.error(org.emotion.detect.enums.ResponseEnum.ERROR, "Failed to process emotion: " + emotionRequest.getEmotion());
            }
        } catch (Exception e) {
            return ResponseVo.error(org.emotion.detect.enums.ResponseEnum.ERROR, "Error processing emotion: " + e.getMessage());
        }
    }

    /**
     * Get twitter sentiment chart data for ECharts visualization
     * Returns daily sentiment data from twitter_comments_time table
     * @return response containing twitter sentiment chart data formatted for ECharts
     */
    @GetMapping("/twitter-line-chart")
    public ResponseVo<SentimentChartResponse> getTwitterSentimentChartData() {
        try {
            SentimentChartResponse response = dashboardService.getTwitterSentimentChartData();
            return ResponseVo.success(response);
        } catch (Exception e) {
            return ResponseVo.error(org.emotion.detect.enums.ResponseEnum.ERROR, "Error getting twitter sentiment chart data: " + e.getMessage());
        }
    }
}
