package com.social.post.services;

import com.social.post.dtos.CreateUserPostDto;
import com.social.post.dtos.UserPostResponseDto;
import com.social.post.entities.UserPost;
import com.social.post.exception.PostSaveException;
import com.social.post.exception.UserProfileNotFoundException;

import java.util.List;

public interface PostService {
    void createPost(CreateUserPostDto userPostDto);
    List<UserPostResponseDto> getUserPostsByUserIdAndLastDays(long userId, int lastDays);
}
