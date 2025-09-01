package org.emotion.detect.service;

import org.emotion.detect.dto.EmotionAnalysisResponse;
import java.util.List;

public interface EmotionAnalysisService {
    EmotionAnalysisResponse analyzeEmotions(List<String> emotionTypes);
}
