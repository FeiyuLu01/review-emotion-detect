package org.emotion.detect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO class representing the questionnaire response
 * Contains a list of questionnaire items
 */
@Data
public class QuestionnaireResponse {
    /** List of questionnaire items */
    @JsonProperty("data")
    private List<QuestionnaireItem> data;

    public QuestionnaireResponse() {}

    public QuestionnaireResponse(List<QuestionnaireItem> data) {
        this.data = data;
    }
    
    public List<QuestionnaireItem> getData() {
        return data;
    }
}
