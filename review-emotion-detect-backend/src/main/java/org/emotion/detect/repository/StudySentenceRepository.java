package org.emotion.detect.repository;

import org.emotion.detect.entity.StudySentence;
import java.util.List;

public interface StudySentenceRepository {
    List<String> findDistinctSentenceTextBySection(String section);
}
