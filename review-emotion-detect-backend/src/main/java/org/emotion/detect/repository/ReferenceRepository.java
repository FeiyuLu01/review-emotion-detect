package org.emotion.detect.repository;

import org.emotion.detect.entity.ReferenceItem;
import java.util.List;

public interface ReferenceRepository {
    ReferenceItem findFirstReferenceBySection(String section);
}
