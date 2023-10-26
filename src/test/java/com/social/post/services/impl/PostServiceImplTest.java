package com.social.post.services.impl;
import com.social.post.dtos.*;
import com.social.post.entities.Content;
import com.social.post.entities.EventPost;
import com.social.post.entities.UserPost;
import com.social.post.enums.PostType;
import com.social.post.exception.PostNotFoundException;
import com.social.post.exception.PostSaveException;
import com.social.post.exception.UserPostsFetchException;
import com.social.post.exception.UserProfileNotFoundException;
import com.social.post.interactors.UserServiceInteractor;
import com.social.post.respositories.EventRepository;
import com.social.post.respositories.PostRepository;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PostServiceImplTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private PostRepository postRepository;
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserServiceInteractor userServiceInteractor;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void testGetAllPosts() {
        List<UserPost> mockPosts = new ArrayList<>();
        Mockito.when(postRepository.findAll()).thenReturn(mockPosts);
        List<UserPost> result = postService.getAllPosts();
        assertEquals(mockPosts, result);
    }

    @Test
    void testCreatePost() {
        long userId = 1L;
        UserProfileResponseDto mockUserProfile = new UserProfileResponseDto();
        mockUserProfile.setUserType("REGULAR");
        when(userServiceInteractor.getUserProfile(userId)).thenReturn(mockUserProfile);

        CreateUserPostDto createUserPostDto = new CreateUserPostDto();
        createUserPostDto.setUserId(userId);
        Content content = new Content();
        content.setValue("Test content");
        createUserPostDto.setContent(content);

        UserPost mockUserPost = new UserPost();
        mockUserPost.setUserId(1);
        mockUserPost.setUserId(userId);

        when(postRepository.save(any(UserPost.class))).thenReturn(mockUserPost);
        assertDoesNotThrow(() -> postService.createPost(createUserPostDto));
    }

    @Test
    void testGetUserPostsByUserIdAndLastDays_UserPostsFetchException() {
        UserProfileResponseDto userProfileResponseDto = new UserProfileResponseDto();
        userProfileResponseDto.setFirstName("Aakash");
        userProfileResponseDto.setUserType("REGULAR");

        when(userServiceInteractor.getUserProfile(101)).thenReturn(userProfileResponseDto);
        when(postRepository.findByUserIdAndCreatedOnGreaterThanEqual(anyLong(), any()))
                .thenThrow(new UserPostsFetchException("Error fetching user posts"));

        UserPostsFetchException exception = assertThrows(UserPostsFetchException.class,
                () -> postService.getUserPostsByUserIdAndLastDays(101, 1));

        assertEquals("Error fetching user posts", exception.getMessage());
    }


@Test
    void testGetUserPostsByUserIdAndLastDays() {
        long userId = 1L;
        int lastDays = 7;

        UserPost mockUserPost = new UserPost();
        mockUserPost.setUserId(1);
        mockUserPost.setPostScore(2);
        Content content = new Content();
        content.setPostType(PostType.TEXT);
        content.setValue("Test content");
        mockUserPost.setContent(content);


        UserProfileResponseDto userProfileResponseDto = new UserProfileResponseDto();
        userProfileResponseDto.setFirstName("Aakash");
        userProfileResponseDto.setUserType("REGULAR");
        List<UserPost> mockUserPosts = Collections.singletonList(mockUserPost);
         when(userServiceInteractor.getUserProfile(userId)).thenReturn(userProfileResponseDto);
        when(postRepository.findByUserIdAndCreatedOnGreaterThanEqual(anyLong(), any())).thenReturn(mockUserPosts);

        assertDoesNotThrow(() -> {
            List<UserPostResponseDto> userPostResponses = postService.getUserPostsByUserIdAndLastDays(userId, lastDays);
            assertNotNull(userPostResponses);
            assertFalse(userPostResponses.isEmpty());
        });
    }


    @Test
    void testUpdateRankingForComments() {
        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setUserId(1L);
        commentRequestDto.setPostId("1");
        commentRequestDto.setComment("This is a comment");

        UserPost mockUserPost = new UserPost();
        mockUserPost.setId("1");
        mockUserPost.setPostScore(1);
        mockUserPost.setUserAndComments(new HashMap<>());
        UserProfileResponseDto mockUserProfile = new UserProfileResponseDto();
        mockUserProfile.setUserType("REGULAR");
        when(userServiceInteractor.getUserProfile(1)).thenReturn(mockUserProfile);
        when(postRepository.findById(commentRequestDto.getPostId())).thenReturn(Optional.of(mockUserPost));
        assertDoesNotThrow(() -> postService.updateRankingForComments(commentRequestDto));
        assertEquals(3, mockUserPost.getPostScore());
        assertTrue(mockUserPost.getUserAndComments().containsKey(commentRequestDto.getUserId()));
        assertEquals(1, mockUserPost.getUserAndComments().get(commentRequestDto.getUserId()).size());
        assertEquals(commentRequestDto.getComment(), mockUserPost.getUserAndComments().get(commentRequestDto.getUserId()).get(0));
    }

    @Test
    void testUpdateRankingForPostLikes() {
        String postId = "1";
        String userId = "2";

        UserPost mockUserPost = new UserPost();
        mockUserPost.setId(postId);
        mockUserPost.setPostScore(1);
        mockUserPost.setUsersLike(new HashSet<>());
        UserProfileResponseDto mockUserProfile = new UserProfileResponseDto();
        mockUserProfile.setUserType("REGULAR");
        when(userServiceInteractor.getUserProfile(2)).thenReturn(mockUserProfile);
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockUserPost));
        assertDoesNotThrow(() -> postService.updateRankingForPostLikes(postId, userId));
        assertEquals(5, mockUserPost.getPostScore());
        assertTrue(mockUserPost.getUsersLike().contains(userId));
    }

    @Test
    void testUpdateRankingForEventLikes() {
        String userId = "1";
        String eventId = "2";

        EventPost mockEventPost = new EventPost();
        mockEventPost.setId(eventId);
        mockEventPost.setPostScore(1);
        mockEventPost.setUsersLike(new HashSet<>());
        UserProfileResponseDto mockUserProfile = new UserProfileResponseDto();
        mockUserProfile.setUserType("REGULAR");
        when(userServiceInteractor.getUserProfile(1)).thenReturn(mockUserProfile);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEventPost));
        assertDoesNotThrow(() -> postService.updateRankingForEventLikes(userId, eventId));
        assertEquals(5, mockEventPost.getPostScore());
        assertTrue(mockEventPost.getUsersLike().contains(userId));
    }
    @Test
    void testCreateEventPost() {
        long userId = 1L;
        UserProfileResponseDto mockUserProfile = new UserProfileResponseDto();
        mockUserProfile.setUserType("CELEBRITY");
        when(userServiceInteractor.getUserProfile(userId)).thenReturn(mockUserProfile);

        CreateUserEventDto createUserEventDto = new CreateUserEventDto();
        createUserEventDto.setUserId(userId);
        Content content = new Content();
        content.setValue("content");
        content.setPostType(PostType.TEXT);
        createUserEventDto.setContent(content);
        createUserEventDto.setEventName("Sample Event");

        EventPost mockEventPost = new EventPost();
        mockEventPost.setUserId(userId);

        when(eventRepository.save(Mockito.any(EventPost.class))).thenReturn(mockEventPost);
        postService.setEventTopicName("eventTopic");
        assertDoesNotThrow(() -> postService.createEventPost(createUserEventDto));
    }

    @Test
    void testCreateEventPost_UserProfileNotFoundException() {
        long userId = 1L;
        when(userServiceInteractor.getUserProfile(userId)).thenThrow(UserProfileNotFoundException.class);

        CreateUserEventDto createUserEventDto = new CreateUserEventDto();
        createUserEventDto.setUserId(userId);

        try {
            postService.createEventPost(createUserEventDto);
            fail("Expected UserProfileNotFoundException, but no exception was thrown.");
        } catch (UserProfileNotFoundException exception) {
            assertEquals("User profile is not found", exception.getMessage());
        }
    }

    @Test
    void testUpdateRankingForComments_PostNotFoundException() {
        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setUserId(1L);
        commentRequestDto.setPostId("1");
        commentRequestDto.setComment("This is a comment");

        when(postRepository.findById(commentRequestDto.getPostId())).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> postService.updateRankingForComments(commentRequestDto));
        assertEquals("Post not found for the id1", exception.getMessage());
    }

    @Test
    void testUpdateRankingForEventLikes_UserProfileNotFoundException() {
        String userId = "1";
        String eventId = "2";
        UserProfileResponseDto mockUserProfile = new UserProfileResponseDto();
        mockUserProfile.setUserType("CELEBRITY");
        when(userServiceInteractor.getUserProfile(1)).thenReturn(mockUserProfile);
        when(userServiceInteractor.getUserProfile(1)).thenThrow(UserProfileNotFoundException.class);

        UserProfileNotFoundException exception = assertThrows(UserProfileNotFoundException.class, () -> postService.updateRankingForEventLikes(userId, eventId));
        assertEquals("User profile is not found", exception.getMessage());
    }
}

