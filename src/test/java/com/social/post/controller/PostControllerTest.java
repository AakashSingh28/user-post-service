package com.social.post.controller;

import com.social.post.dtos.*;
import com.social.post.entities.Content;
import com.social.post.entities.UserPost;
import com.social.post.enums.PostType;
import com.social.post.exception.PostNotFoundException;
import com.social.post.exception.PostSaveException;
import com.social.post.exception.PostServiceException;
import com.social.post.services.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostServiceImpl postService;

    @Test
    void testGetAllPosts() throws Exception {

        List<UserPost> mockPosts = new ArrayList<>();
        Mockito.when(postService.getAllPosts()).thenReturn(mockPosts);


        ResultActions resultActions = mockMvc.perform(get("/api/v1/posts"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreatePost() throws Exception {

        Mockito.doNothing().when(postService).createPost(Mockito.any(CreateUserPostDto.class));

        CreateUserPostDto createUserPostDto = new CreateUserPostDto();
        Content content = new Content();
        content.setValue("Test content");
        content.setPostType(PostType.TEXT);
        createUserPostDto.setContent(content);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": {\"value\": \"Test content\", \"postType\": \"TEXT\"}}"));


        resultActions
                .andExpect(status().isCreated())
                .andExpect(content().string("Post created successfully"));
    }

    @Test
    void testGetPostByUserId() throws Exception {

        List<UserPostResponseDto> mockUserPosts = new ArrayList<>();
        Mockito.when(postService.getUserPostsByUserIdAndLastDays(anyLong(), anyInt())).thenReturn(mockUserPosts);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/posts/1/1"));

        resultActions
                .andExpect(status().isOk());
    }

    @Test
    void testGetPostByUserId_PostNotFoundException() throws Exception {
        Mockito.when(postService.getUserPostsByUserIdAndLastDays(anyLong(), anyInt()))
                .thenThrow(new PostNotFoundException("Posts not found"));

        ResultActions resultActions = mockMvc.perform(get("/api/v1/posts/1/1"));

        resultActions
                .andExpect(status().isNotFound());
    }


    @Test
    void testCreatePost_InternalServerError() throws Exception {
        Mockito.doThrow(new PostServiceException("Internal Server Error"))
                .when(postService).createPost(Mockito.any(CreateUserPostDto.class));

        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": {\"value\": \"Test content\", \"postType\": \"TEXT\"}}"));

        resultActions
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateEvent() throws Exception {
        Mockito.doNothing().when(postService).createEventPost(Mockito.any(CreateUserEventDto.class));

        CreateUserEventDto createUserEventDto = new CreateUserEventDto();
        Content content = new Content();
        content.setValue("Test event content");
        content.setPostType(PostType.TEXT);
        createUserEventDto.setContent(content);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": {\"value\": \"Test event content\", \"postType\": \"TEXT\"}}"));

        resultActions
                .andExpect(status().isCreated())
                .andExpect(content().string("Post created successfully"));
    }

    @Test
    void testGetEventByUserId() throws Exception {
        List<UserEventResponseDto> mockUserEvents = new ArrayList<>();
        Mockito.when(postService.getUserEventsByUserIdAndLastDays(anyLong(), anyInt())).thenReturn(mockUserEvents);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/posts/event/1/1"));

        resultActions
                .andExpect(status().isOk());
    }


    @Test
    void testCreatePost_Exception() throws Exception {
        Mockito.doThrow(new PostServiceException("Internal Server Error"))
                .when(postService).createPost(Mockito.any(CreateUserPostDto.class));

        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": {\"value\": \"Test content\", \"postType\": \"TEXT\"}}"));

        resultActions
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateEvent_Exception() throws Exception {
        Mockito.doThrow(new PostServiceException("Internal Server Error"))
                .when(postService).createEventPost(Mockito.any(CreateUserEventDto.class));

        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"userId\": 102,\n" +
                        "    \"userLocation\": \"Bangalore\",\n" +
                        "    \"eventName\": \"My First Event\",\n" +
                        "    \"content\": {\n" +
                        "    \"postType\": \"IMAGE\",\n" +
                        "    \"value\": \"s3:/location/\"\n" +
                        "     },\n" +
                        "    \"eventStartDate\": \"2023-14-18T12:00:00.000Z\",\n" +
                        "    \"eventEndDate\": \"2023-15-19T12:00:00.000Z\"\n" +
                        "}\n"));

         resultActions.andExpect(status().isInternalServerError());
    }



    @Test
    void testGetEventByUserId_PostNotFoundException() throws Exception {
        Mockito.when(postService.getUserEventsByUserIdAndLastDays(anyLong(), anyInt()))
                .thenThrow(new PostNotFoundException("Events not found"));

        ResultActions resultActions = mockMvc.perform(get("/api/v1/posts/event/1/1"));

        resultActions
                .andExpect(status().isNotFound());
    }
    @Test
    void testUpdateRankingForPostLike() throws Exception {
        Mockito.doNothing().when(postService).updateRankingForPostLikes(Mockito.anyString(), Mockito.anyString());

        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/post/like/1/2"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("Post ranking updated successfully"));
    }

    @Test
    void testUpdateRankingForEventLike() throws Exception {
        Mockito.doNothing().when(postService).updateRankingForEventLikes(Mockito.anyString(), Mockito.anyString());

        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/event/like/1/2"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("Post ranking updated successfully"));
    }

    @Test
    void testUpdateRankingForComment() throws Exception {
        Mockito.doNothing().when(postService).updateRankingForComments(Mockito.any(UserCommentRequestDto.class));

        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setUserId(100l);
        commentRequestDto.setPostId("2");
        commentRequestDto.setComment("This is a comment");

        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": \"1\", \"postId\": \"2\", \"comment\": \"This is a comment\"}"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("Post ranking updated successfully"));
    }


}
