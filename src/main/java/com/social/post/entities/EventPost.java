package com.social.post.entities;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "events")
public class EventPost extends Post {
    @Id
    private String id;
    private String eventName;
    private Date eventStartDate;
    private Date eventEndDate;

    @PrePersist
    private void prePersist(){
        this.createdOn = new Date();
    }

    @PreUpdate
    private void preUpdate(){
        this.updatedOn = new Date();
    }
}