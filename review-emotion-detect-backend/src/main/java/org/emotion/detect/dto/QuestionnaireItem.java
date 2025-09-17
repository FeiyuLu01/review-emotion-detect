package org.emotion.detect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO class representing a questionnaire item
 * Contains the review text and its corresponding emotion type
 */
@Data
public class QuestionnaireItem {
    /** The emotion type (e.g., "sad", "happy", "angry") */
    @JsonProperty("type")
    private String type;
    /** The review text content */
    @JsonProperty("review")
    private String review;

    public QuestionnaireItem() {}

    public QuestionnaireItem(String type, String review) {
        this.type = type;
        this.review = review;
    }
}
