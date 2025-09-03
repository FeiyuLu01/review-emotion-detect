package org.emotion.detect.service.impl;

import org.emotion.detect.dto.EmotionAnalysisResponse;
import org.emotion.detect.entity.ReferenceItem;
import org.emotion.detect.repository.ReferenceRepository;
import org.emotion.detect.repository.StudySentenceRepository;
import org.emotion.detect.service.EmotionAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of emotion analysis service
 * Handles the business logic for analyzing emotions and gathering related data
 */
@Service
public class EmotionAnalysisServiceImpl implements EmotionAnalysisService {

    @Autowired
    private StudySentenceRepository studySentenceRepository;

    @Autowired
    private ReferenceRepository referenceRepository;

    /**
     * Analyze emotions for each provided emotion type
     * Gets sentences and references for each emotion type and combines them into analysis
     */
    @Override
    public EmotionAnalysisResponse analyzeEmotions(List<String> emotionTypes) {
        EmotionAnalysisResponse response = new EmotionAnalysisResponse();
        List<EmotionAnalysisResponse.EmotionAnalysisItem> analysisItems = new ArrayList<>();

        // Process each emotion type
        for (String emotionType : emotionTypes) {
            EmotionAnalysisResponse.EmotionAnalysisItem item = new EmotionAnalysisResponse.EmotionAnalysisItem();
            item.setType(emotionType);

            // Get sentences for this emotion type
            List<String> sentences = studySentenceRepository.findDistinctSentenceTextBySection(emotionType);
            if (sentences.isEmpty()) {
                item.setAnalysis("No analysis data available for this emotion type at the moment");
            } else {
                String analysis = String.join(" ", sentences);
                item.setAnalysis(analysis);
            }

            // Get reference information for this emotion type
            ReferenceItem reference = referenceRepository.findFirstReferenceBySection(emotionType);
            EmotionAnalysisResponse.ReferenceInfo referenceInfo = getReferenceInfo(reference);

            item.setReference(referenceInfo);
            analysisItems.add(item);
        }

        response.setData(analysisItems);
        return response;
    }

    /**
     * Helper method to convert ReferenceItem to ReferenceInfo
     * Handles null references gracefully
     */
    private static EmotionAnalysisResponse.ReferenceInfo getReferenceInfo(ReferenceItem reference) {
        EmotionAnalysisResponse.ReferenceInfo referenceInfo = new EmotionAnalysisResponse.ReferenceInfo();

        if (reference == null) {
            referenceInfo.setTitle("No relevant literature available for this emotion type");
            referenceInfo.setUrl("");
            referenceInfo.setCitation("");
        } else {
            referenceInfo.setTitle(reference.getTitle() != null ? reference.getTitle() : "");
            referenceInfo.setUrl(reference.getUrl() != null ? reference.getUrl() : "");
            referenceInfo.setCitation(reference.getCitation() != null ? reference.getCitation() : "");
        }
        return referenceInfo;
    }
}
