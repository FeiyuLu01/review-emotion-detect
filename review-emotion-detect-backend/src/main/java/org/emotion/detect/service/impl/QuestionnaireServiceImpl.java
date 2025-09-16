package org.emotion.detect.service.impl;

import org.emotion.detect.dto.LevelFeedbackResponse;
import org.emotion.detect.dto.QuestionnaireItem;
import org.emotion.detect.dto.QuestionnaireResponse;
import org.emotion.detect.entity.EmotionalLevel;
import org.emotion.detect.entity.LabeledComment;
import org.emotion.detect.repository.EmotionalLevelRepository;
import org.emotion.detect.repository.QuestionnaireRepository;
import org.emotion.detect.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of QuestionnaireService
 * Handles business logic for questionnaire generation
 */
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private EmotionalLevelRepository emotionalLevelRepository;

    @Override
    public QuestionnaireResponse generateQuestionnaire(String mode) {
        // Determine the number of questions based on mode
        int questionCount = getQuestionCountByMode(mode);
        
        // Get random labeled comments from database
        List<LabeledComment> labeledComments = questionnaireRepository.findRandomLabeledComments(questionCount);
        
        // Convert to questionnaire items - use fine_labels as the type
        List<QuestionnaireItem> questionnaireItems = labeledComments.stream()
                .map(comment -> new QuestionnaireItem(comment.getFineLabels(), comment.getTextNatural()))
                .collect(Collectors.toList());
        
        return new QuestionnaireResponse(questionnaireItems);
    }

    @Override
    public LevelFeedbackResponse getLevelFeedback(int level) {
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Invalid level: " + level + ". Valid levels are: 1, 2, 3, 4");
        }

        // Map frontend level to database level text
        String levelText = mapLevelToText(level);
        
        // Query database with level text
        EmotionalLevel emotionalLevel = emotionalLevelRepository.findByLevelText(levelText);
        if (emotionalLevel == null) {
            throw new IllegalArgumentException("No data found for level: " + level);
        }

        return new LevelFeedbackResponse(
            emotionalLevel.getTestFeedback(),
            emotionalLevel.getTips(),
            emotionalLevel.getRefs(),
            emotionalLevel.getGrowthTips(),
            emotionalLevel.getRefs() // Using refs as growth_refs since the table doesn't have growth_refs column
        );
    }

    public String mapLevelToText(int level) {
        switch (level) {
            case 1:
                return "Advanced (8)";
            case 2:
                return "Beginner (0-4)";
            case 3:
                return "Expert (9-10)";
            case 4:
                return "Intermediate (5-7)";
            default:
                throw new IllegalArgumentException("Invalid level: " + level + ". Valid levels are: 1, 2, 3, 4");
        }
    }

    /**
     * Get the number of questions based on the mode
     * @param mode the difficulty mode
     * @return number of questions to generate
     */
    private int getQuestionCountByMode(String mode) {
        switch (mode.toUpperCase()) {
            case "EASY":
                return 7;
            case "STANDARD":
                return 15;
            case "ADVANCED":
                return 25;
            default:
                throw new IllegalArgumentException("Invalid mode: " + mode + ". Valid modes are: Easy, Standard, Advanced");
        }
    }
}
