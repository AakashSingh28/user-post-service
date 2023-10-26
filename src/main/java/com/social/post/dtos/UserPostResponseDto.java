package com.social.post.dtos;
import com.social.post.enums.PostType;
import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class UserPostResponseDto {

    private String content;

    private String userLocation;

    private int postScore;

    private String postType;

    private Set<String> usersLike;

    private boolean isEventPost;

    private Map<Long, List<String>> userAndComments;
}
