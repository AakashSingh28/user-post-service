package com.social.post.notification.kafka.configuration;

import com.social.post.notification.kafka.serializer.configuration.KafkaConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = KafkaConfiguration.class)
@TestPropertySource(properties = {
        "kafka.bootstrap-servers=localhost:9092",
        "kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
public class KafkaConfigurationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void testKafkaTemplateShouldNotBeNull() {
        assertNotNull(kafkaTemplate);
    }
}
