package com.social.post.services.impl;

import com.social.post.dtos.*;
import com.social.post.entities.EventPost;
import com.social.post.entities.UserPost;
import com.social.post.exception.*;
import com.social.post.interactors.UserServiceInteractor;
import com.social.post.notification.kafka.serializer.event.PostCreatedEvent;
import com.social.post.respositories.EventRepository;
import com.social.post.respositories.PostRepository;
import com.social.post.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final EventRepository eventRepository;
    private final UserServiceInteractor userServiceInteractor;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.post.topic-name}")
    private String postTopicName;
    @Value("${kafka.event.topic-name}")
    private String EventTopicName;

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
        userPost.setUserLocation(userPostDto.getUserLocation());
        try {
            UserProfileResponseDto userProfileResponseDto = userServiceInteractor.getUserProfile(userPost.getUserId());

            if(userProfileResponseDto == null){
                throw new UserProfileNotFoundException("User profile is not found");
            }
                log.info("Response from user service: {}", userProfileResponseDto);
                String userType = userProfileResponseDto.getUserType();
                switch (userType) {
                    case "REGULAR" -> userPost.setPostScore(1);
                    case "CELEBRITY" -> userPost.setPostScore(2);
                    case "POLITICIAN" -> userPost.setPostScore(3);
                }

                postRepository.save(userPost);
                log.info("Post created successfully");

                // Notifying Kafka about the new post
                PostCreatedEvent postCreatedEvent = new PostCreatedEvent(String.valueOf(userPost.getUserId()), String.valueOf(userPost.getUserId()));
                kafkaTemplate.send(postTopicName, postCreatedEvent);

                log.info("Sent PostCreatedEvent to Kafka");
        }catch (UserProfileNotFoundException e){
            log.error("Error processing post creation: {}", e.getMessage(), e);
            throw new UserProfileNotFoundException("User profile is not found");
        }
        catch (PostSaveException e) {
            log.error("Error processing post creation: {}", e.getMessage());
            throw new PostSaveException("Error creating or saving post",e.getCause());
        }
    }

    public void createEventPost(CreateUserEventDto createUserPostDto) {
        log.info("Creating a new {} event for user: {}", createUserPostDto.getEventName(), createUserPostDto.getUserId());
        EventPost eventPost = new EventPost();
        eventPost.setUserId(createUserPostDto.getUserId());
        eventPost.setContent(createUserPostDto.getContent());
        eventPost.setEventName(createUserPostDto.getEventName());
        eventPost.setEventStartDate(createUserPostDto.getEventEndDate());
        eventPost.setEventEndDate(createUserPostDto.getEventEndDate());
        eventPost.setUserLocation(createUserPostDto.getUserLocation());
        try {
            UserProfileResponseDto userProfileResponseDto = userServiceInteractor.getUserProfile(eventPost.getUserId());
            if(userProfileResponseDto == null){
                throw new UserProfileNotFoundException("User profile is not found");
            }
                log.info("Response from user service: {}", userProfileResponseDto);
                String userType = userProfileResponseDto.getUserType();
                switch (userType) {
                    case "REGULAR" -> eventPost.setPostScore(1);
                    case "CELEBRITY" -> eventPost.setPostScore(2);
                    case "POLITICIAN" -> eventPost.setPostScore(3);
                }

                eventRepository.save(eventPost);
                log.info("Post created successfully");

                // Notifying Kafka about the new post
                PostCreatedEvent postCreatedEvent = new PostCreatedEvent(String.valueOf(eventPost.getId()), String.valueOf(eventPost.getUserId()));
                kafkaTemplate.send(EventTopicName, postCreatedEvent);

                log.info("Sent PostCreatedEvent to Kafka");

        } catch (UserProfileNotFoundException ex) {
            log.error("Error processing post creation: {}", ex.getMessage());
            throw new PostSaveException("Error creating or saving post", ex.getCause());
        }catch (PostSaveException ex) {
            log.error("Error processing post creation: {}", ex.getMessage());
            throw new PostSaveException("Error creating or saving post", ex.getCause());
        }
    }


    @Override
    public List<UserPostResponseDto> getUserPostsByUserIdAndLastDays(long userId, int lastDays) throws UserPostsFetchException {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC")).minusDays(lastDays);
        Date startDate = Date.from(zonedDateTime.toInstant());
        log.info("Fetching posts for user {} created in the last {} days", userId, lastDays);

        try {
            UserProfileResponseDto userProfileResponseDto = userServiceInteractor.getUserProfile(userId);
            if(userProfileResponseDto == null){
                throw new UserProfileNotFoundException("User profile is not found");
            }

            List<UserPostResponseDto> userPostResponseDto = new ArrayList<>();
            List<UserPost> userPosts = postRepository.findByUserIdAndCreatedOnGreaterThanEqual(userId, startDate);

            for (UserPost userPost : userPosts) {
                UserPostResponseDto userPostResponse = new UserPostResponseDto();
                userPostResponse.setPostScore(userPost.getPostScore());
                userPostResponse.setContent(userPost.getContent().getValue());
                userPostResponse.setUserAndComments(userPost.getUserAndComments());
                userPostResponse.setUsersLike(userPost.getUsersLike());
                userPostResponse.setUserLocation(userPost.getUserLocation());
                userPostResponseDto.add(userPostResponse);
            }

            return userPostResponseDto;

        } catch (UserProfileNotFoundException e) {
            log.error("Error while fetching posts for user {}: {}", userId, e.getMessage());
            throw new UserPostsFetchException("Error fetching user posts", e.getCause());
        }catch (PostServiceException e) {
            log.error("Error while fetching posts for user {}: {}", userId, e.getMessage());
            throw new UserPostsFetchException("Error fetching user posts", e.getCause());
        }
    }

    @Override
    public List<UserEventResponseDto> getUserEventsByUserIdAndLastDays(long userId, int lastDays) throws UserPostsFetchException {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC")).minusDays(lastDays);
        Date startDate = Date.from(zonedDateTime.toInstant());
        log.info("Fetching posts for user {} created in the last {} days", userId, lastDays);

        try {
            UserProfileResponseDto userProfileResponseDto = userServiceInteractor.getUserProfile(userId);
            if(userProfileResponseDto == null){
                throw new UserProfileNotFoundException("User profile is not found");
            }

            List<UserEventResponseDto> userEventResponseDtos = new ArrayList<>();
            List<EventPost> eventPosts = eventRepository.findByUserIdAndCreatedOnGreaterThanEqual(userId, startDate);

            for (EventPost eventPost : eventPosts) {
                UserEventResponseDto userEventResponseDto = new UserEventResponseDto();
                userEventResponseDto.setEventName(eventPost.getEventName());
                userEventResponseDto.setEventStartDate(eventPost.getEventStartDate());
                userEventResponseDto.setEventEndDate(eventPost.getEventEndDate());
                userEventResponseDto.setPostScore(eventPost.getPostScore());
                userEventResponseDto.setContent(eventPost.getContent().getValue());
                userEventResponseDto.setUserAndComments(eventPost.getUserAndComments());
                userEventResponseDto.setUsersLike(eventPost.getUsersLike());
                userEventResponseDto.setUserLocation(eventPost.getUserLocation());
                userEventResponseDtos.add(userEventResponseDto);
            }

            return userEventResponseDtos;

        } catch (UserProfileNotFoundException e) {
            log.error("Error while fetching posts for user {}: {}", userId, e.getMessage(), e);
            throw new UserProfileNotFoundException("Error fetching user posts", e.getCause());
        }catch (PostServiceException e) {
            log.error("Error while fetching posts for user {}: {}", userId, e.getMessage());
            throw new PostServiceException("Error fetching user posts", e.getCause());
        }
    }

    public void updateRankingForComments(UserCommentRequestDto commentRequestDto) {
        try {
            Optional<UserPost> optionalUserPost = postRepository.findById(commentRequestDto.getPostId());

            if(optionalUserPost.isEmpty()){
                throw new PostNotFoundException("Post not found for the id"+commentRequestDto.getPostId());
            }

                UserPost userPost = optionalUserPost.get();
                int newRanking = userPost.getPostScore() + 2;
                userPost.setPostScore(newRanking);

                Map<Long,List<String>> userAndComments = userPost.getUserAndComments();

                if(userAndComments.get(commentRequestDto.getUserId())!= null){
                    userAndComments.get(commentRequestDto.getUserId()).add(commentRequestDto.getComment());
                }else {
                    userAndComments.put(commentRequestDto.getUserId(),List.of(commentRequestDto.getComment()));
                }
                userPost.setUpdatedOn(new Date());
                userPost.setUserAndComments(userAndComments);

                postRepository.save(userPost);

                log.info("Post ranking updated successfully for post with ID: {}", commentRequestDto.getComment());

        } catch (UserProfileNotFoundException e) {
            log.error("Error updating post ranking based on likes: {}", e.getMessage(), e.getMessage());
            throw new UserProfileNotFoundException("Failed to update post ranking based on likes", e.getCause());
        }catch (PostServiceException e) {
            log.error("Error updating post ranking based on likes: {}", e.getMessage(), e.getMessage());
            throw new PostServiceException("Failed to update post ranking based on likes", e.getCause());
        }
    }

    public void updateRankingForPostLikes(String postId, String userId) {
        try {
            Optional<UserPost> optionalUserPost = postRepository.findById(postId);
            if(optionalUserPost.isEmpty()){
                throw new PostNotFoundException("Post not found for the id"+postId);
            }
                UserPost userPost = optionalUserPost.get();
                int newRanking = userPost.getPostScore() + 4;
                Set<String> updatedLikes = userPost.getUsersLike();
                updatedLikes.add(userId);
                userPost.setPostScore(newRanking);
                userPost.setUsersLike(updatedLikes);
                userPost.setUpdatedOn(new Date());
                postRepository.save(userPost);

                log.info("Post ranking updated successfully for post with ID: {}", postId);

        } catch (UserProfileNotFoundException e) {
            log.error("Error updating post ranking based on likes: {}", e.getMessage(), e);
            throw new UserProfileNotFoundException("Failed to update post ranking based on likes", e);
        }catch (PostServiceException e) {
            log.error("Error updating post ranking based on likes: {}", e.getMessage(), e);
            throw new PostServiceException("Failed to update post ranking based on likes", e);
        }
    }

    public void updateRankingForEventLikes(String userId, String eventId) {
        {
            try {
                Optional<EventPost> optionalUserPost = eventRepository.findById(eventId);
                 if(optionalUserPost.isEmpty()){
                     throw new PostNotFoundException("Post not found for the id"+eventId);
                 }
                    EventPost eventPost = optionalUserPost.get();
                    int newRanking = eventPost.getPostScore() + 4;
                    Set<String> updatedLikes = eventPost.getUsersLike();
                    if(updatedLikes == null) updatedLikes = new HashSet<>();
                    updatedLikes.add(userId);
                    eventPost.setPostScore(newRanking);
                    eventPost.setUsersLike(updatedLikes);
                    eventPost.setUpdatedOn(new Date());
                    eventRepository.save(eventPost);
                    log.info("Event ranking updated successfully for post with ID: {}", eventId);

            } catch (UserProfileNotFoundException e) {
                log.error("Error updating event ranking based on likes: {}", e.getMessage(), e.getMessage());
                throw new UserProfileNotFoundException("Failed to update event ranking based on likes", e.getCause());
            }catch (PostServiceException e) {
                log.error("Error updating event ranking based on likes: {}", e.getMessage(), e.getMessage());
                throw new PostServiceException("Failed to update event ranking based on likes", e.getCause());
            }
        }
    }
}
