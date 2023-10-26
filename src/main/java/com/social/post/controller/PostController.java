package com.social.post.controller;

import com.social.post.dtos.*;
import com.social.post.entities.UserPost;
import com.social.post.exception.PostNotFoundException;
import com.social.post.exception.PostServiceException;
import com.social.post.exception.UserProfileNotFoundException;
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


    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody @Valid CreateUserPostDto createUserPostDto) {
        logger.info("Creating a new post");
        try {
            postService.createPost(createUserPostDto);
            return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);
        }
        catch (UserProfileNotFoundException e){
            log.error("Error processing post creation: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to create post", HttpStatus.NOT_FOUND);
        }
        catch (PostServiceException e) {
            log.error("Error processing post creation: {}", e.getMessage());
            logger.error("Error creating post: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to create post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/event")
    public ResponseEntity<String> createEvent(@RequestBody @Valid CreateUserEventDto createUserPostDto) {
        logger.info("Creating a new post");
        try {
            postService.createEventPost(createUserPostDto);
            return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);
        } catch (UserProfileNotFoundException e){
            log.error("Error processing post creation: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to create post", HttpStatus.NOT_FOUND);
        }
        catch (PostServiceException e) {
            log.error("Error processing post creation: {}", e.getMessage());
            logger.error("Error creating post: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to create post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("{userId}/{lastDays}")
    public ResponseEntity<List<UserPostResponseDto>> getPostByUserId(@PathVariable long userId,
                                                                     @PathVariable int lastDays) {
        try {
            logger.info("Fetching posts for user with ID: {} for the last {} days", userId, lastDays);
            List<UserPostResponseDto> userPosts = postService.getUserPostsByUserIdAndLastDays(userId, lastDays);
            logger.info("Posts fetched successfully");
            return new ResponseEntity<>(userPosts, HttpStatus.OK);

        } catch (PostNotFoundException ex) {
            logger.warn("PostNotFoundException: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (PostServiceException e) {
            logger.error("Error creating post: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/event/{userId}/{lastDays}")
    public ResponseEntity<List<UserEventResponseDto>> getEventByUserId(@PathVariable long userId,
                                                                       @PathVariable int lastDays) {
        try {
            logger.info("Fetching posts for user with ID: {} for the last {} days", userId, lastDays);
            List<UserEventResponseDto> userPosts = postService.getUserEventsByUserIdAndLastDays(userId, lastDays);
            logger.info("Posts fetched successfully");
            return new ResponseEntity<>(userPosts, HttpStatus.OK);

        } catch (PostNotFoundException e) {
            logger.warn("PostNotFoundException: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (UserProfileNotFoundException e){
            log.error("Error processing post creation: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (PostServiceException e) {
            logger.error("Error while not fetching event {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("post/like/{userId}/{postId}")
    public ResponseEntity<String> updateRankingForPostLike(@PathVariable String postId,
                                                           @PathVariable String userId) {
        logger.info("Updating post ranking");
        try {
            postService.updateRankingForPostLikes(postId,userId);
            return ResponseEntity.ok("Post ranking updated successfully");
        } catch (UserProfileNotFoundException e){
            log.error("Error processing post creation: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (PostNotFoundException e) {
            logger.warn("PostNotFoundException: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (PostServiceException e) {
            logger.error("Error updating post ranking: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update post ranking");
        }
    }

    @PostMapping("event/like/{userId}/{eventId}")
    public ResponseEntity<String> updateRankingForEventLike(@PathVariable String userId,
                                                            @PathVariable String eventId) {
        logger.info("Updating post ranking");
        try {
            postService.updateRankingForEventLikes(userId,eventId);
            return ResponseEntity.ok("Post ranking updated successfully");
        } catch (UserProfileNotFoundException e){
            log.error("Error processing post creation: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PostNotFoundException e) {
            logger.warn("PostNotFoundException: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PostServiceException e) {
            logger.error("Error updating post ranking: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update post ranking");
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<String> updateRankingForComment(@RequestBody @Valid UserCommentRequestDto commentRequestDto) {
        logger.info("Updating post ranking");
        try {
            postService.updateRankingForComments(commentRequestDto);
            return ResponseEntity.ok("Post ranking updated successfully");
        } catch (UserProfileNotFoundException e) {
            logger.error("Error updating post ranking: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to update post ranking");
        } catch (PostNotFoundException e) {
            logger.warn("PostNotFoundException: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (PostServiceException e) {
            logger.error("Error updating post ranking: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update post ranking");
        }
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



}
