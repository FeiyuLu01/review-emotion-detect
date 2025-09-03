package org.emotion.detect.entity;

/**
 * Entity class representing a study sentence
 * This holds data about sentences used for emotion analysis
 */
public class StudySentence {
    /** Unique identifier for the sentence */
    private Integer sentenceId;
    /** Section/category this sentence belongs to */
    private String section;
    /** The actual text content of the sentence */
    private String sentenceText;
    /** Emotion label assigned to this sentence */
    private String goemotionLabel;

    public Integer getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(Integer sentenceId) {
        this.sentenceId = sentenceId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSentenceText() {
        return sentenceText;
    }

    public void setSentenceText(String sentenceText) {
        this.sentenceText = sentenceText;
    }

    public String getGoemotionLabel() {
        return goemotionLabel;
    }

    public void setGoemotionLabel(String goemotionLabel) {
        this.goemotionLabel = goemotionLabel;
    }
}
