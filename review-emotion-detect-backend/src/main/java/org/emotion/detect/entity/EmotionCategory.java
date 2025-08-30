package org.emotion.detect.entity;

import lombok.Data;

@Data
public class EmotionCategory {
    private String type;
    private String analysis;

    public EmotionCategory(String t, String s) {
        type = t;
        analysis = s;
    }
}
