package com.social.post.services.impl;

import com.social.post.dtos.CreateUserPostDto;
import com.social.post.dtos.UserPostResponseDto;
import com.social.post.dtos.UserProfileResponseDto;
import com.social.post.entities.UserPost;
import com.social.post.exception.PostSaveException;
import com.social.post.exception.UserPostsFetchException;
import com.social.post.exception.UserProfileNotFoundException;
import com.social.post.interactors.UserServiceInteractor;
import com.social.post.notification.kafka.serializer.event.PostCreatedEvent;
import com.social.post.respositories.PostRepository;
import com.social.post.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserServiceInteractor userServiceInteractor;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic-name}")
    private String topicName;

    public List<UserPost> getAllPosts() {
        log.info("Fetching all posts");
        return postRepository.findAll();
    }

    public UserPost getPostById(Long postId) {
        log.info("Fetching post by ID: {}", postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> new UserProfileNotFoundException("Post not found with ID: " + postId));
    }

    public void createPost(CreateUserPostDto userPostDto){
        log.info("Creating a new post for user: {}", userPostDto.getUserId());

        UserPost userPost = new UserPost();
        userPost.setUserId(userPostDto.getUserId());
        userPost.setContent(userPostDto.getContent());

        try {
            UserProfileResponseDto userProfileResponseDto = userServiceInteractor.getUserProfile(userPost.getUserId());
            if (userProfileResponseDto != null) {
                log.info("Response from user service: {}", userProfileResponseDto);
                String userType = userProfileResponseDto.getUserType();
                switch (userType) {
                    case "REGULAR" -> userPost.setPostScore(1);
                    case "CELEBRITY" -> userPost.setPostScore(2);
                    case "POLITICIAN" -> userPost.setPostScore(3);
                }

                postRepository.save(userPost);
                log.info("Post created successfully");

                // Notify Kafka about the new post
                PostCreatedEvent postCreatedEvent = new PostCreatedEvent(String.valueOf(userPost.getId()), String.valueOf(userPost.getUserId()));
                kafkaTemplate.send(topicName, postCreatedEvent);

                log.info("Sent PostCreatedEvent to Kafka");
            }
        } catch (PostSaveException ex) {
            log.error("Error processing post creation: {}", ex.getMessage(), ex);
            throw new PostSaveException("Error creating or saving post", ex);
        }
    }


    @Override
    public List<UserPostResponseDto> getUserPostsByUserIdAndLastDays(long userId, int lastDays) throws UserPostsFetchException {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC")).minusDays(lastDays);
        Date startDate = Date.from(zonedDateTime.toInstant());
        log.info("Fetching posts for user {} created in the last {} days", userId, lastDays);

        try {
            List<UserPostResponseDto> userPostResponseDto = new ArrayList<>();
            List<UserPost> userPosts = postRepository.findByUserIdAndCreatedOnGreaterThanEqual(userId, startDate);

            for (UserPost userPost : userPosts) {
                UserPostResponseDto userPostResponse = new UserPostResponseDto();
                userPostResponse.setContent(userPost.getContent());
                userPostResponse.setPostScore(userPost.getPostScore());
                userPostResponseDto.add(userPostResponse);
            }

            return userPostResponseDto;

        } catch (Exception exception) {
            log.error("Error while fetching posts for user {}: {}", userId, exception.getMessage(), exception);
            throw new UserPostsFetchException("Error fetching user posts", exception);
        }
    }

}
