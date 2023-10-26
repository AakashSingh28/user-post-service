package com.social.post.services;

import com.social.post.dtos.*;

import java.util.List;

public interface PostService {
    void createPost(CreateUserPostDto userPostDto);
    List<UserPostResponseDto> getUserPostsByUserIdAndLastDays(long userId, int lastDays);
    List<UserEventResponseDto> getUserEventsByUserIdAndLastDays(long userId, int lastDays);
    void updateRankingForPostLikes(String postId, String userId);
    void updateRankingForComments(UserCommentRequestDto commentRequestDto);
    void createEventPost(CreateUserEventDto createUserPostDto);

    void updateRankingForEventLikes(String userId, String eventId);
}
