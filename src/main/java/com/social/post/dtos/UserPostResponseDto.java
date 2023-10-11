package com.social.post.dtos;

import com.social.post.entities.Content;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPostResponseDto {
    private Content content;
    private int postScore;
}
