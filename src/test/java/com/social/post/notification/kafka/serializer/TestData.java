package com.social.post.notification.kafka.serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public  class TestData {
    private String postId;
    private String userId;
}