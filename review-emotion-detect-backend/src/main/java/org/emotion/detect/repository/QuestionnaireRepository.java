package org.emotion.detect.repository;

import org.emotion.detect.entity.LabeledComment;
import java.util.List;

/**
 * Repository interface for questionnaire-related database operations
 * Handles queries for labeled comments used in questionnaire generation
 */
public interface QuestionnaireRepository {
    /**
     * Get random labeled comments for questionnaire generation
     * @param limit the number of comments to retrieve
     * @return list of random labeled comments
     */
    List<LabeledComment> findRandomLabeledComments(int limit);
}
