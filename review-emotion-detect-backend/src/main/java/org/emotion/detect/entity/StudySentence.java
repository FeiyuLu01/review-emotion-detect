package org.emotion.detect.entity;

public class StudySentence {
    private Integer sentenceId;
    private String section;
    private String sentenceText;
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
