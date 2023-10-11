package com.social.post.entities;

import com.social.post.enums.PostType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Content {
    @NotNull
    private PostType PostType;
    @NotNull
    private String value;
}
