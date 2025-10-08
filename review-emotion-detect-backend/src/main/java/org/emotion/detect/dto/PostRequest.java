package org.emotion.detect.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * DTO class for creating a new anonymous post
 */
@Data
public class PostRequest {
    /** Content of the anonymous post */
    @NotBlank(message = "Post content cannot be empty")
    private String content;
    
    /** Background color in hexadecimal format (e.g., #D8D8D8) */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Background color must be a valid hex color (e.g., #D8D8D8)")
    private String bgColor;
    
    // Manual getters and setters for compatibility
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getBgColor() { return bgColor; }
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }
}
