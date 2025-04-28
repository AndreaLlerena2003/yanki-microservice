package nnt_data.yanki_service.infrastructure.persistence.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nnt_data.yanki_service.domain.service.KafkaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaResponseListener {

    private final KafkaService kafkaService;

    @KafkaListener(
            topics = "${kafka.topics.debit-card-validation-response}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenForDebitCardValidationResponses(MessageWrapper<?> message) {
        log.debug("Received message on debit card validation response topic: {}", message);
        kafkaService.handleResponse(message);
    }

    @KafkaListener(
            topics = "${kafka.topics.transaction-responses}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenForTransactionResponses(MessageWrapper<?> message) {
        log.debug("Received message on transaction response topic: {}", message);
        kafkaService.handleResponse(message);
    }
}