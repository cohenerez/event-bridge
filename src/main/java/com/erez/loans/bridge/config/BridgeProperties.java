package com.erez.loans.bridge.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * @param salesforce connection to the org: OAuth client credentials and the Pub/Sub API endpoint
 * @param topic      the platform event channel to subscribe to
 * @param kafkaTopic where loan events are published
 * @param batchSize  how many events to request from Salesforce at a time (flow control)
 * @param replayFile where the last processed replay ID is kept, so a restart resumes without gaps
 */
@ConfigurationProperties("bridge")
public record BridgeProperties(
        @DefaultValue Salesforce salesforce,
        @DefaultValue("/event/LoanStatusChanged__e") String topic,
        @DefaultValue("loan-events") String kafkaTopic,
        @DefaultValue("100") int batchSize,
        @DefaultValue("./data/replay-id") String replayFile,
        @DefaultValue("true") boolean subscriptionEnabled,
        @DefaultValue("5s") Duration reconnectDelay) {

    /**
     * @param loginUrl     the org's My Domain URL, e.g. https://example.my.salesforce.com (client credentials
     *                     tokens must be requested from My Domain, not login.salesforce.com)
     * @param clientId     consumer key of the External Client App
     * @param clientSecret consumer secret of the External Client App
     */
    public record Salesforce(
            String loginUrl,
            String clientId,
            String clientSecret,
            @DefaultValue("api.pubsub.salesforce.com") String pubsubHost,
            @DefaultValue("7443") int pubsubPort) {
    }
}
