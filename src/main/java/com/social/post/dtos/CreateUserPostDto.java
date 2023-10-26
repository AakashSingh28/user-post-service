package com.social.post.dtos;

import com.social.post.entities.Content;
import com.social.post.enums.PostType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateUserPostDto {
  //  @NotNull
  //  private long postId;
    @NotNull
    private long userId;

    @NotNull
    private Content content;

    private String userLocation;
}
