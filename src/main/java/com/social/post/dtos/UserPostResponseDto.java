package com.social.post.dtos;
import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class UserPostResponseDto {

    private String content;

    private String userLocation;

    private int postScore;

    private Set<String> usersLike;

    private Map<Long, List<String>> userAndComments;
}
