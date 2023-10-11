package com.social.post.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "users_post")
public class UserPost {

    @Id
    private String id;

    private long userId;
    private Date createdOn;
    private Date updatedOn;

    private Content content;
    private int postScore;

    public UserPost() {
        this.createdOn = new Date();
        this.updatedOn = new Date();
    }
}
