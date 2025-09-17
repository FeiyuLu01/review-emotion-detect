package org.emotion.detect.repository;

import org.emotion.detect.entity.EmotionalLevel;

/**
 * Repository interface for emotional level database operations
 * Handles queries for emotional level feedback data
 */
public interface EmotionalLevelRepository {
    /**
     * Get emotional level data by level text
     * @param levelText the level text (e.g., "Advanced (8)", "Beginner (0-4)")
     * @return emotional level data
     */
    EmotionalLevel findByLevelText(String levelText);
}
