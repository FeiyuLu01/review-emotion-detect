package org.emotion.detect.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO class representing a questionnaire item
 * Contains the review text and its corresponding emotion type
 */
@Getter
@Setter
public class QuestionnaireItem {
    /** The emotion type (e.g., "sad", "happy", "angry") */
    private String type;
    /** The review text content */
    private String review;

    public QuestionnaireItem() {}

    public QuestionnaireItem(String type, String review) {
        this.type = type;
        this.review = review;
    }
}
