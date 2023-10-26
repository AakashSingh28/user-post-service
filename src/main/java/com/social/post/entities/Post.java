package com.social.post.entities;

import jakarta.persistence.Id;
import lombok.Data;

import java.util.*;

@Data
public abstract class Post {

    protected long userId;
    protected Date createdOn;
    protected Date updatedOn;
    protected Content content;
    protected int postScore;
    protected String userLocation;
    protected Map<Long, List<String>> userAndComments;
    protected Set<String> usersLike;

    public Post() {
        if (createdOn == null) {
            createdOn = new Date();
        }
        updatedOn = new Date();
        usersLike = new HashSet<>();
        userAndComments = new HashMap<>();
    }
}
