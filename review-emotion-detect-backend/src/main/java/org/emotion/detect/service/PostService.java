package org.emotion.detect.service;

import org.emotion.detect.dto.PostRequest;
import org.emotion.detect.entity.Post;
import java.util.List;

/**
 * Service interface for Post operations
 * Handles business logic for anonymous posts
 */
public interface PostService {
    
    /**
     * Create a new anonymous post
     * @param postRequest the post data to create
     * @return the created post
     */
    Post createPost(PostRequest postRequest);
    
    /**
     * Get all anonymous posts
     * @return list of all posts ordered by creation time (newest first)
     */
    List<Post> getAllPosts();
}
