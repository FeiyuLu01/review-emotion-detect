package org.emotion.detect.service;

import org.emotion.detect.dto.EmotionAnalysisResponse;
import java.util.List;

/**
 * Service interface for emotion analysis operations
 * Defines the contract for analyzing emotions based on different types
 */
public interface EmotionAnalysisService {
    /**
     * Analyze emotions for the given emotion types
     * @param emotionTypes list of emotion types to analyze
     * @return response containing analysis results for each emotion type
     */
    EmotionAnalysisResponse analyzeEmotions(List<String> emotionTypes);
}

