package org.emotion.detect.controller;

import org.emotion.detect.dto.QuestionnaireResponse;
import org.emotion.detect.service.QuestionnaireService;
import org.emotion.detect.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling questionnaire requests
 * Provides endpoints for generating emotion detection questionnaires
 */
@RestController
@RequestMapping("/emotion")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;

    /**
     * Generate questionnaire based on the specified mode
     * @param mode the difficulty mode ("Easy", "Standard", "Advanced")
     * @return response containing questionnaire data
     */
    @GetMapping("/questionnaire")
    public ResponseVo<QuestionnaireResponse> getQuestionnaire(@RequestParam("mode") String mode) {
        try {
            QuestionnaireResponse response = questionnaireService.generateQuestionnaire(mode);
            return ResponseVo.success(response);
        } catch (IllegalArgumentException e) {
            return ResponseVo.error(org.emotion.detect.enums.ResponseEnum.ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseVo.error(org.emotion.detect.enums.ResponseEnum.ERROR, "Error generating questionnaire: " + e.getMessage());
        }
    }
}
