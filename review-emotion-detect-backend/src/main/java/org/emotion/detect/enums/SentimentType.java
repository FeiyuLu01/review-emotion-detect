package org.emotion.detect.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Simple enum for emotion sentiment classification
 * Contains the 28 emotions grouped by sentiment type
 */
public enum SentimentType {
    
    POSITIVE("positive", Arrays.asList(
        "joy", "excitement", "love", "admiration", "amusement", "approval", 
        "gratitude", "pride", "relief", "optimism", "caring", "curiosity", 
        "surprise", "realization"
    )),
    
    NEGATIVE("negative", Arrays.asList(
        "sadness", "disappointment", "grief", "remorse", "embarrassment", 
        "fear", "nervousness", "anger", "annoyance", "disapproval", "disgust"
    )),
    
    NEUTRAL("neutral", Arrays.asList(
        "neutral"
    ));
    
    private final String value;
    private final List<String> emotions;
    
    SentimentType(String value, List<String> emotions) {
        this.value = value;
        this.emotions = emotions;
    }
    
    public String getValue() {
        return value;
    }
    
    public List<String> getEmotions() {
        return emotions;
    }
    
    /**
     * Get sentiment type for a given emotion
     * @param emotion the emotion to classify
     * @return the sentiment type as string, or "neutral" if not found
     */
    public static String getSentimentTypeString(String emotion) {
        if (emotion == null) {
            return "neutral";
        }
        
        String lowerEmotion = emotion.toLowerCase();
        
        for (SentimentType type : SentimentType.values()) {
            if (type.emotions.contains(lowerEmotion)) {
                return type.value;
            }
        }
        
        return "neutral";
    }
}
