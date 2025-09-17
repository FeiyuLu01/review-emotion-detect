package org.emotion.detect.entity;

/**
 * Entity class representing emotional_levels table
 * Contains level feedback data for different emotional detection levels
 */
public class EmotionalLevel {
    /** Level identifier (e.g., "Advanced (8)", "Beginner (0-4)") */
    private String level;
    /** Test feedback message */
    private String testFeedback;
    /** Tips for improvement */
    private String tips;
    /** References for further learning */
    private String refs;
    /** Growth tips for the user */
    private String growthTips;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTestFeedback() {
        return testFeedback;
    }

    public void setTestFeedback(String testFeedback) {
        this.testFeedback = testFeedback;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getRefs() {
        return refs;
    }

    public void setRefs(String refs) {
        this.refs = refs;
    }

    public String getGrowthTips() {
        return growthTips;
    }

    public void setGrowthTips(String growthTips) {
        this.growthTips = growthTips;
    }
}
