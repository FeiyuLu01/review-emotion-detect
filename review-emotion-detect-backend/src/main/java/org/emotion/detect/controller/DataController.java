package org.emotion.detect.controller;

import org.emotion.detect.dto.EmotionAnalysisResponse;
import org.emotion.detect.service.EmotionAnalysisService;
import org.emotion.detect.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DataController {

    @Autowired
    private EmotionAnalysisService emotionAnalysisService;

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



