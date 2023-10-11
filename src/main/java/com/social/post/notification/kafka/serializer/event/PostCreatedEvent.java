package com.social.post.notification.kafka.serializer.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostCreatedEvent {
    private String postId;
    private String userId;
}
