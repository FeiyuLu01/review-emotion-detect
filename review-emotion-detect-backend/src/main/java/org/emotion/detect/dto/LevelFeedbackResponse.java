package org.emotion.detect.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO class representing the level feedback response
 * Contains feedback information based on user's level
 */


@Getter
@Setter
public class LevelFeedbackResponse {
    /** Feedback message for the user */
    private String feedback;
    /** Tips for improvement */
    private String tips;
    /** References for further learning */
    private String refs;
    /** Growth tips for the user */
    private String growthTips;
    /** Growth references for the user */
    private String growthRefs;

    public LevelFeedbackResponse() {}

    public LevelFeedbackResponse(String feedback, String tips, String refs, String growthTips, String growthRefs) {
        this.feedback = feedback;
        this.tips = tips;
        this.refs = refs;
        this.growthTips = growthTips;
        this.growthRefs = growthRefs;
    }

}
