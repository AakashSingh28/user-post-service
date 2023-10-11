package com.social.post.services.impl;
import com.social.post.dtos.CreateUserPostDto;
import com.social.post.dtos.UserPostResponseDto;
import com.social.post.dtos.UserProfileResponseDto;
import com.social.post.entities.Content;
import com.social.post.entities.UserPost;
import com.social.post.exception.PostSaveException;
import com.social.post.exception.UserPostsFetchException;
import com.social.post.exception.UserProfileNotFoundException;
import com.social.post.interactors.UserServiceInteractor;
import com.social.post.respositories.PostRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    void testGetPostById() {
        Long postId = 1L;
        UserPost mockPost = new UserPost();
        mockPost.setId("1");
        Mockito.when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(mockPost));
        UserPost result = postService.getPostById(postId);
        assertEquals(mockPost, result);
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
        mockUserPost.setId("1");
        mockUserPost.setUserId(userId);

        when(postRepository.save(any(UserPost.class))).thenReturn(mockUserPost);
        assertDoesNotThrow(() -> postService.createPost(createUserPostDto));
    }

    @Test
    void testGetUserPostsByUserIdAndLastDays_UserPostsFetchException() {
        when(postRepository.findByUserIdAndCreatedOnGreaterThanEqual(anyLong(), any()))
                .thenThrow(new RuntimeException("Error fetching user posts"));

        UserPostsFetchException exception = assertThrows(UserPostsFetchException.class,
                () -> postService.getUserPostsByUserIdAndLastDays(anyLong(), anyInt()));

        assertEquals("Error fetching user posts", exception.getMessage());

        assertNotNull(exception.getCause());
    }


@Test
    void testGetUserPostsByUserIdAndLastDays() {
        long userId = 1L;
        int lastDays = 7;

        UserPost mockUserPost = new UserPost();
        mockUserPost.setUserId(1);
        mockUserPost.setPostScore(2);
        Content content = new Content();
        content.setValue("Test content");
        mockUserPost.setContent(content);


        List<UserPost> mockUserPosts = Collections.singletonList(mockUserPost);

        when(postRepository.findByUserIdAndCreatedOnGreaterThanEqual(anyLong(), any())).thenReturn(mockUserPosts);

        assertDoesNotThrow(() -> {
            List<UserPostResponseDto> userPostResponses = postService.getUserPostsByUserIdAndLastDays(userId, lastDays);
            assertNotNull(userPostResponses);
            assertFalse(userPostResponses.isEmpty());
        });
    }

    @Test
    void testGetPostById_PostNotFoundException() {
        Long postId = 1L;
        Mockito.when(postRepository.findById(postId)).thenReturn(java.util.Optional.empty());
        assertThrows(UserProfileNotFoundException.class, () -> postService.getPostById(postId));
    }

    @Test
    void testCreatePost_UserProfileNotFoundException() {
        long userId = 1L;
        when(userServiceInteractor.getUserProfile(userId)).thenThrow(PostSaveException.class);

        CreateUserPostDto createUserPostDto = new CreateUserPostDto();
        createUserPostDto.setUserId(userId);

        try {
            postService.createPost(createUserPostDto);
            fail("Expected PostSaveException, but no exception was thrown.");
        } catch (PostSaveException exception) {
            assertEquals("Error creating or saving post" , exception.getMessage());
        }
    }

}
