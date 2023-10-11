package com.social.post.controller;

import com.social.post.dtos.CreateUserPostDto;
import com.social.post.dtos.UserPostResponseDto;
import com.social.post.entities.Content;
import com.social.post.entities.UserPost;
import com.social.post.enums.PostType;
import com.social.post.exception.PostNotFoundException;
import com.social.post.services.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerMvcTest {

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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
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
        Mockito.doThrow(new RuntimeException("Internal Server Error"))
                .when(postService).createPost(Mockito.any(CreateUserPostDto.class));

        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": {\"value\": \"Test content\", \"postType\": \"TEXT\"}}"));

        resultActions
                .andExpect(status().isInternalServerError());
    }

}
