package com.social.post.dtos;

import lombok.Data;

import java.util.Date;
@Data
public class UserEventResponseDto extends UserPostResponseDto{
    private String eventName;
    private Date eventStartDate;
    private Date eventEndDate;
}
