package org.emotion.detect.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * DTO class for emotion analysis request
 * Contains the emotion to be analyzed and stored
 */
@Data
public class EmotionRequest {
    /** The emotion keyword to be analyzed */
    @NotBlank(message = "Emotion cannot be empty")
    private String emotion;

    // Manual getters and setters for compatibility
    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }

    public EmotionRequest() {}

    public EmotionRequest(String emotion) {
        this.emotion = emotion;
    }
}
