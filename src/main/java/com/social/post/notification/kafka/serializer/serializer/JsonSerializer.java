package com.social.post.notification.kafka.serializer.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;
@NoArgsConstructor
public class JsonSerializer implements Serializer<Object> {

    private  ObjectMapper objectMapper = new ObjectMapper();

    public JsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object", e);
        }
    }
}
