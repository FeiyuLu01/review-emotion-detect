package org.emotion.detect.dto;

import lombok.Data;

/**
 * DTO class representing the level feedback response
 * Contains feedback information based on user's level
 */

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

    // Getters and Setters
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    
    public String getTips() { return tips; }
    public void setTips(String tips) { this.tips = tips; }
    
    public String getRefs() { return refs; }
    public void setRefs(String refs) { this.refs = refs; }
    
    public String getGrowthTips() { return growthTips; }
    public void setGrowthTips(String growthTips) { this.growthTips = growthTips; }
    
    public String getGrowthRefs() { return growthRefs; }
    public void setGrowthRefs(String growthRefs) { this.growthRefs = growthRefs; }

}
