package org.emotion.detect.service.impl;

import org.emotion.detect.dto.PostRequest;
import org.emotion.detect.entity.Post;
import org.emotion.detect.repository.PostRepository;
import org.emotion.detect.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of PostService
 * Handles business logic for anonymous posts
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public Post createPost(PostRequest postRequest) {
        // Create Post entity from request
        Post post = new Post();
        post.setContent(postRequest.getContent());
        post.setBgColor(postRequest.getBgColor());
        
        // Save to database and return
        return postRepository.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}
