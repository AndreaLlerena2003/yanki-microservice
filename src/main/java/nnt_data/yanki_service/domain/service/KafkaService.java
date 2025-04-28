package nnt_data.yanki_service.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nnt_data.yanki_service.infrastructure.persistence.kafka.MessageWrapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, Sinks.One<Object>> pendingResponses = new ConcurrentHashMap<>();

    /**
     * Sends a message to a request topic and waits for a response on a response topic
     *
     * @param requestTopic The topic to send the request to
     * @param responseTopic The topic where the response will be received
     * @param payload The payload to send
     * @param responseType The expected response type
     * @param timeout The maximum time to wait for a response
     * @return A Mono that will emit the response or error if timeout occurs
     */
    public <T, R> Mono<R> sendAndReceive(String requestTopic, String responseTopic,
                                         T payload, Class<R> responseType, Duration timeout) {
        String correlationId = UUID.randomUUID().toString();
        log.debug("Preparing to send message with correlationId: {} to topic: {}", correlationId, requestTopic);

        // Create a sink to receive the response
        Sinks.One<Object> responseSink = Sinks.one();
        pendingResponses.put(correlationId, responseSink);

        // Create a message wrapper with the correlation ID
        MessageWrapper<T> wrapper = new MessageWrapper<>(payload, correlationId);

        // Build the kafka message
        Message<MessageWrapper<T>> message = MessageBuilder
                .withPayload(wrapper)
                .setHeader(KafkaHeaders.TOPIC, requestTopic)
                .setHeader(KafkaHeaders.KEY, correlationId)
                .build();

        // Send the message
        return Mono.fromCallable(() -> {
                    log.debug("Sending message to topic: {} with correlationId: {}", requestTopic, correlationId);
                    kafkaTemplate.send(message);
                    return correlationId;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(id -> responseSink.asMono()
                        .timeout(timeout)
                        .doFinally(signalType -> {
                            log.debug("Removing response sink for correlationId: {}", correlationId);
                            pendingResponses.remove(correlationId);
                        })
                )
                // Replace direct cast with proper conversion
                .flatMap(response -> {
                    try {
                        log.debug("Converting response for correlationId: {}: {}", correlationId, response);
                        // If it's already the correct type, just return it
                        if (responseType.isInstance(response)) {
                            return Mono.just(responseType.cast(response));
                        }
                        // Otherwise, convert using ObjectMapper
                        R convertedResponse = objectMapper.convertValue(response, responseType);
                        return Mono.just(convertedResponse);
                    } catch (Exception e) {
                        log.error("Error converting response for correlationId: {}", correlationId, e);
                        return Mono.error(new RuntimeException("Failed to convert response", e));
                    }
                })
                .doOnError(error -> log.error("Error in sendAndReceive operation for correlationId: {}", correlationId, error))
                .doOnSuccess(response -> log.debug("Received response for correlationId: {}: {}", correlationId, response));
    }

    /**
     * Handle response messages received from Kafka
     * This method should be called by a Kafka listener
     *
     * @param message The received message
     */
    public void handleResponse(MessageWrapper<?> message) {
        String correlationId = message.getCorrelationId();
        log.debug("Received response with correlationId: {}", correlationId);

        Sinks.One<Object> sink = pendingResponses.get(correlationId);
        if (sink != null) {
            log.debug("Found pending request for correlationId: {}, emitting response", correlationId);
            sink.tryEmitValue(message.getPayload());
        } else {
            log.warn("No pending request found for correlationId: {}, response will be ignored", correlationId);
        }
    }
}