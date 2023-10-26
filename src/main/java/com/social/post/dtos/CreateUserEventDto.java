package com.social.post.dtos;

import com.social.post.entities.Content;
import com.social.post.enums.PostType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
@Data
public class CreateUserEventDto {

    @NotNull
    private long userId;

    @NotNull
    private Content content;
    @NotNull
    private String userLocation;

    @NotNull
    private String eventName;
    @NotNull
    private Date eventStartDate;
    @NotNull
    private Date eventEndDate;
}
