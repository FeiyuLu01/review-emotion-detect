package org.emotion.detect.entity;

/**
 * Entity class representing a labeled comment from the labeled_comments table
 * This holds data about comments with their emotion labels for questionnaire generation
 */
public class LabeledComment {
    /** Unique identifier for the comment */
    private Integer id;
    /** Natural text content of the comment (corresponds to text_natural field) */
    private String textNatural;
    /** Fine emotion label assigned to this comment (corresponds to fine_labels field) */
    private String fineLabels;

    private String scenario;
    
    /** Ekman emotion label assigned to this comment (corresponds to ekman_labels field) */
    private String ekmanLabels;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTextNatural() {
        return textNatural;
    }

    public void setTextNatural(String textNatural) {
        this.textNatural = textNatural;
    }

    public String getFineLabels() {
        return fineLabels;
    }

    public void setFineLabels(String fineLabels) {
        this.fineLabels = fineLabels;
    }

    public String getEkmanLabels() {
        return ekmanLabels;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public void setEkmanLabels(String ekmanLabels) {
        this.ekmanLabels = ekmanLabels;
    }
}
