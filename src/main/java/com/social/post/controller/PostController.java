package com.social.post.controller;

import com.social.post.dtos.CreateUserPostDto;
import com.social.post.dtos.UserPostResponseDto;
import com.social.post.entities.UserPost;
import com.social.post.exception.PostNotFoundException;
import com.social.post.services.impl.PostServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostServiceImpl postService;

    @Autowired
    public PostController(PostServiceImpl postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<UserPost>> getAllPosts() {
        try {
            logger.info("Fetching all posts");
            List<UserPost> posts = postService.getAllPosts();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all posts: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody @Valid CreateUserPostDto createUserPostDto) {
        logger.info("Creating a new post");
        try {
            postService.createPost(createUserPostDto);
            return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating post: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to create post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("{userId}/{lastDays}")
    public ResponseEntity<List<UserPostResponseDto>> getPostByUserId(@PathVariable long userId, @PathVariable int lastDays) {
        try {
            logger.info("Fetching posts for user with ID: {} for the last {} days", userId, lastDays);
            List<UserPostResponseDto> userPosts = postService.getUserPostsByUserIdAndLastDays(userId, lastDays);
            logger.info("Posts fetched successfully");
            return new ResponseEntity<>(userPosts, HttpStatus.OK);

        } catch (PostNotFoundException ex) {
            logger.warn("PostNotFoundException: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
