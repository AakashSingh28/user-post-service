package com.social.post.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users_post")
public class UserPost extends Post{

    @Id
    private String id;


}
