package org.emotion.detect.dto;

import java.util.List;

public class EmotionAnalysisResponse {
    private List<EmotionAnalysisItem> data;

    public List<EmotionAnalysisItem> getData() {
        return data;
    }

    public void setData(List<EmotionAnalysisItem> data) {
        this.data = data;
    }

    public static class EmotionAnalysisItem {
        private String type;
        private String analysis;
        private ReferenceInfo reference;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }

        public ReferenceInfo getReference() {
            return reference;
        }

        public void setReference(ReferenceInfo reference) {
            this.reference = reference;
        }
    }

    public static class ReferenceInfo {
        private String title;
        private String url;
        private String citation;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCitation() {
            return citation;
        }

        public void setCitation(String citation) {
            this.citation = citation;
        }
    }
}
