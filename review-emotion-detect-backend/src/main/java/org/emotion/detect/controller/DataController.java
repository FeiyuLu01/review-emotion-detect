package org.emotion.detect.controller;

import org.emotion.detect.dto.EmotionAnalysisResponse;
import org.emotion.detect.service.EmotionAnalysisService;
import org.emotion.detect.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling emotion analysis requests
 * This is the main API endpoint that the frontend calls
 */
@RestController
public class DataController {

    @Autowired
    private EmotionAnalysisService emotionAnalysisService;

    /**
     * Analyze emotions based on the provided emotion types
     * @param emotionTypes list of emotion types to analyze
     * @return response containing emotion analysis results
     */
    @PostMapping("/emotion_analysis")
    public ResponseVo<EmotionAnalysisResponse> getEmotionAnalysis(@RequestBody List<String> emotionTypes) {
        try {
            EmotionAnalysisResponse response = emotionAnalysisService.analyzeEmotions(emotionTypes);
            return ResponseVo.success(response);
        } catch (Exception e) {
            return ResponseVo.error(org.emotion.detect.enums.ResponseEnum.ERROR, "Error processing emotion analysis: " + e.getMessage());
        }
    }
}



