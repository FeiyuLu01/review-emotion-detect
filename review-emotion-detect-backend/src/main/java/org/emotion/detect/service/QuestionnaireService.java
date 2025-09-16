package org.emotion.detect.service;

import org.emotion.detect.dto.LevelFeedbackResponse;
import org.emotion.detect.dto.QuestionnaireResponse;

/**
 * Service interface for questionnaire-related business logic
 * Handles questionnaire generation based on different difficulty modes
 */
public interface QuestionnaireService {
    /**
     * Generate questionnaire based on the specified mode
     * @param mode the difficulty mode ("Easy", "Standard", "Advanced")
     * @return questionnaire response containing random questions
     */
    QuestionnaireResponse generateQuestionnaire(String mode);
    
    /**
     * Get level feedback based on the user's level
     * @param level the user's level (1-4)
     * @return LevelFeedbackResponse with appropriate data
     */
    LevelFeedbackResponse getLevelFeedback(int level);
}
