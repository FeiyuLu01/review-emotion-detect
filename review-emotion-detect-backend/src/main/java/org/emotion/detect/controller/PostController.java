package org.emotion.detect.controller;

import org.emotion.detect.dto.PostRequest;
import org.emotion.detect.entity.Post;
import org.emotion.detect.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for handling anonymous post requests
 * Provides endpoints for creating and retrieving anonymous posts
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * Create a new anonymous post
     * @param postRequest the post data to create
     * @return response containing the created post
     */
    @PostMapping("/add-post")
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequest postRequest) {
        try {
            Post post = postService.createPost(postRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all anonymous posts
     * @return response containing list of all posts
     */
    @GetMapping("/get-all")
    public ResponseEntity<List<Post>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
