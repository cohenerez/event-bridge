package com.erez.loans.bridge.config;

import com.erez.loans.bridge.auth.SessionProvider;
import com.erez.loans.bridge.checkpoint.FileReplayStore;
import com.erez.loans.bridge.checkpoint.ReplayStore;
import com.erez.loans.bridge.kafka.EventSink;
import com.erez.loans.bridge.pubsub.EventDecoder;
import com.erez.loans.bridge.pubsub.LoanEventSubscriber;
import com.erez.loans.bridge.pubsub.PubSubCallCredentials;
import com.erez.loans.bridge.pubsub.SchemaCache;
import com.salesforce.eventbus.protobuf.PubSubGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class BridgeConfiguration {

    @Bean
    public NewTopic loanEventsTopic(BridgeProperties properties) {
       return TopicBuilder.name(properties.kafkaTopic()).partitions(3).replicas(1).build();
    }

    @Bean
    public ReplayStore replayStore(BridgeProperties properties) {
        return new FileReplayStore(Path.of(properties.replayFile()));
    }

    @Bean
    public EventDecoder eventDecoder() {
        return new EventDecoder();
    }

    @Bean
    @ConditionalOnProperty(name = "bridge.subscription-enabled", havingValue = "true", matchIfMissing = true)
    public ManagedChannel pubSubChannel(BridgeProperties properties) {
        return ManagedChannelBuilder
                .forAddress(properties.salesforce().pubsubHost(), properties.salesforce().pubsubPort())
                .useTransportSecurity()
                .keepAliveTime(60, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "bridge.subscription-enabled", havingValue = "true", matchIfMissing = true)
    public LoanEventSubscriber loanEventSubscriber(ManagedChannel channel, SessionProvider sessions, EventDecoder decoder,
                                                   EventSink sink, ReplayStore replayStore, BridgeProperties properties) {
        PubSubCallCredentials credentials = new PubSubCallCredentials(sessions);
        SchemaCache schemas = new SchemaCache(PubSubGrpc.newBlockingStub(channel).withCallCredentials(credentials));
        return new LoanEventSubscriber(
                PubSubGrpc.newStub(channel).withCallCredentials(credentials),
                schemas, decoder, sink, replayStore, sessions,
                properties.topic(), properties.batchSize(), properties.reconnectDelay());
    }
}
