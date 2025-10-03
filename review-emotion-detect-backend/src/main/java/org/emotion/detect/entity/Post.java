package org.emotion.detect.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity class for anonymous posts from the database
 */
@Data
public class Post {
    /** Primary key, auto-incrementing post ID */
    private Integer postId;
    /** Content of the anonymous post */
    private String content;
    /** Background color in hexadecimal format (e.g., #D8D8D8) */
    private String bgColor;
    /** Timestamp when the post was created */
    private LocalDateTime createdAt;
    
    // Manual getters and setters for compatibility
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getBgColor() { return bgColor; }
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
