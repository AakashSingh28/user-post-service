package com.social.post.notification.kafka.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.post.notification.kafka.serializer.serializer.JsonSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class JsonSerializerTest {

    private JsonSerializer jsonSerializer;


    @BeforeEach
    void setUp() {
        jsonSerializer = new JsonSerializer(new ObjectMapper());
    }

    @Test
    void testSerialize() {
        TestData testData = new TestData("John", "25");
        byte[] serializedData = jsonSerializer.serialize("post-topic", testData);

        byte[] expectedBytes = serializeWithObjectMapper(testData);

        assertArrayEquals(expectedBytes, serializedData);
    }

    private byte[] serializeWithObjectMapper(TestData data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object with ObjectMapper", e);
        }
    }

    @Test
    public void testSerializationException() throws JsonProcessingException {
        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(mapper.writeValueAsBytes(Mockito.any())).thenThrow(JsonProcessingException.class);

        JsonSerializer jsonSerializer = new JsonSerializer(mapper);

        TestData testData = new TestData(null, null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jsonSerializer.serialize("post-topic", testData);
        });
    }
}
