package org.emotion.detect.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity class for daily_keywords table
 * Represents keyword records with timestamps
 */
@Data
public class DailyKeyword {
    /** Primary key, auto-incrementing ID */
    private Integer id;
    /** The keyword text */
    private String keyword;
    /** Timestamp when the keyword was recorded */
    private LocalDateTime recordDate;

    // Manual getters and setters for compatibility
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public LocalDateTime getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDateTime recordDate) { this.recordDate = recordDate; }

    public DailyKeyword() {}

    public DailyKeyword(Integer id, String keyword, LocalDateTime recordDate) {
        this.id = id;
        this.keyword = keyword;
        this.recordDate = recordDate;
    }
}
