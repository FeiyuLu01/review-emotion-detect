package org.emotion.detect.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO class representing the questionnaire response
 * Contains a list of questionnaire items
 */
@Getter
@Setter
public class QuestionnaireResponse {
    /** List of questionnaire items */
    private List<QuestionnaireItem> data;

    public QuestionnaireResponse() {}

    public QuestionnaireResponse(List<QuestionnaireItem> data) {
        this.data = data;
    }
}
