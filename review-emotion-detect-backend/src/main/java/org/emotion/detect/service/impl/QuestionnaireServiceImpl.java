package org.emotion.detect.service.impl;

import org.emotion.detect.dto.QuestionnaireItem;
import org.emotion.detect.dto.QuestionnaireResponse;
import org.emotion.detect.entity.LabeledComment;
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
