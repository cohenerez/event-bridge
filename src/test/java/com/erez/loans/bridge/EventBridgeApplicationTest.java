package com.erez.loans.bridge;

import static org.assertj.core.api.Assertions.assertThat;

import com.erez.loans.bridge.kafka.EventSink;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest(properties = {"bridge.subscription-enabled=false", "spring.kafka.admin.auto-create=false"})
public class EventBridgeApplicationTest {

    @Autowired
    private EventSink sink;

    @Test
    void contextLoads() {
        assertThat(sink).isNotNull();
    }


}
