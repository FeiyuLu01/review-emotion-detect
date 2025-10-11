package org.emotion.detect.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO class for sentiment chart response
 * Contains data formatted for ECharts visualization
 */
@Data
public class SentimentChartResponse {
    /** List of dates in format suitable for ECharts (e.g., ["2024.9.10", "2024.9.11", "2024.9.12"]) */
    private List<String> date;
    /** List of negative sentiment counts for each date */
    private List<Integer> negative;
    /** List of positive sentiment counts for each date */
    private List<Integer> positive;
    /** List of neutral sentiment counts for each date */
    private List<Integer> neutral;

    // Manual getters and setters for compatibility
    public List<String> getDate() { return date; }
    public void setDate(List<String> date) { this.date = date; }

    public List<Integer> getNegative() { return negative; }
    public void setNegative(List<Integer> negative) { this.negative = negative; }

    public List<Integer> getPositive() { return positive; }
    public void setPositive(List<Integer> positive) { this.positive = positive; }

    public List<Integer> getNeutral() { return neutral; }
    public void setNeutral(List<Integer> neutral) { this.neutral = neutral; }

    public SentimentChartResponse() {}

    public SentimentChartResponse(List<String> date, List<Integer> negative, List<Integer> positive, List<Integer> neutral) {
        this.date = date;
        this.negative = negative;
        this.positive = positive;
        this.neutral = neutral;
    }
}

