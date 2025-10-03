package org.emotion.detect.repository;

import org.emotion.detect.entity.Post;
import java.util.List;

/**
 * Repository interface for Post entity
 * Handles database operations for anonymous posts
 */
public interface PostRepository {
    
    /**
     * Save a new post to the database
     * @param post the post entity to save
     * @return the saved post with generated ID
     */
    Post save(Post post);
    
    /**
     * Find all posts from the database
     * @return list of all posts ordered by creation time (newest first)
     */
    List<Post> findAll();
}
