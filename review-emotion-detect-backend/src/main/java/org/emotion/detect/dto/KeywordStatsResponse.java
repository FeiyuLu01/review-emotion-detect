package org.emotion.detect.dto;

import lombok.Data;
import java.util.Map;

/**
 * DTO class for keyword statistics response
 * Contains keyword frequency statistics sorted by count
 */
@Data
public class KeywordStatsResponse {
    /** Map of keywords to their occurrence counts, sorted by count descending */
    private Map<String, Integer> keywordStats;

    /** The time period that was analyzed */
    private String timePeriod;

    /** Total number of keywords analyzed */
    private Integer totalKeywords;

    // Manual getters and setters for compatibility
    public Map<String, Integer> getKeywordStats() { return keywordStats; }
    public void setKeywordStats(Map<String, Integer> keywordStats) { this.keywordStats = keywordStats; }

    public String getTimePeriod() { return timePeriod; }
    public void setTimePeriod(String timePeriod) { this.timePeriod = timePeriod; }

    public Integer getTotalKeywords() { return totalKeywords; }
    public void setTotalKeywords(Integer totalKeywords) { this.totalKeywords = totalKeywords; }

    public KeywordStatsResponse() {}

    public KeywordStatsResponse(Map<String, Integer> keywordStats, String timePeriod, Integer totalKeywords) {
        this.keywordStats = keywordStats;
        this.timePeriod = timePeriod;
        this.totalKeywords = totalKeywords;
    }
}

