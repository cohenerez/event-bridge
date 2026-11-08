package com.erez.loans.bridge.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;


@ConfigurationProperties("bridge")
public record BridgeProperties(
        @DefaultValue Salesforce salesforce,
        @DefaultValue("/event/LoanStatusChanged__e") String topic,
        @DefaultValue("loan-events") String kafkaTopic,
        @DefaultValue("100") int batchSize,
        @DefaultValue("./data/replay-id") String replayFile,
        @DefaultValue("true") boolean subscriptionEnabled,
        @DefaultValue("5s") Duration reconnectDelay) {


    public record Salesforce(
            String loginUrl,
            String clientId,
            String clientSecret,
            @DefaultValue("api.pubsub.salesforce.com") String pubsubHost,
            @DefaultValue("7443") int pubsubPort) {
    }
}
