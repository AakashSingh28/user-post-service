package com.social.post.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserCommentRequestDto {
    @NotNull
    long userId;
    @NotNull
    String postId;
    @NotNull
    String comment;
}
