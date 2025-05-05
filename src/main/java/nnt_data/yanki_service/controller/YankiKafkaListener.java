package nnt_data.yanki_service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nnt_data.yanki_service.domain.service.KafkaService;
import nnt_data.yanki_service.domain.service.UserYankiService;
import nnt_data.yanki_service.infrastructure.persistence.kafka.MessageWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class YankiKafkaListener {

    private final KafkaService kafkaService;
    private final UserYankiService userYankiService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.yanki-validation-response}")
    private String yankiResponseTopic;

    @KafkaListener(
            topics = "${kafka.topics.yanki-validation-request}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenForYankiValidationRequests(String message) {
        try {
            MessageWrapper<String> wrapper = objectMapper.readValue(
                    message,
                    new TypeReference<MessageWrapper<String>>() {}
            );

            String userId = wrapper.getPayload();
            String correlationId = wrapper.getCorrelationId();

            userYankiService.existById(userId)
                    .doOnNext(exists -> {
                        try {
                            MessageWrapper<Boolean> responseWrapper =
                                    new MessageWrapper<>(exists, correlationId);
                            kafkaService.send(yankiResponseTopic, responseWrapper)
                                    .subscribe();
                            log.info("Respuesta de validación enviada para usuario Yanki {}: {}",
                                    userId, exists);
                        } catch (Exception e) {
                            log.error("Error al enviar respuesta de validación: {}",
                                    e.getMessage());
                        }
                    })
                    .doOnError(error -> {
                        log.error("Error al validar usuario Yanki {}: {}",
                                userId, error.getMessage());
                        MessageWrapper<Boolean> errorWrapper =
                                new MessageWrapper<>(false, correlationId);
                        kafkaService.send(yankiResponseTopic, errorWrapper)
                                .subscribe();
                    })
                    .subscribe();
        } catch (Exception e) {
            log.error("Error al procesar mensaje de validación: {}", e.getMessage());
        }
    }


}
