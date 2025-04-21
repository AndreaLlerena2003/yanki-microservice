package nnt_data.customer_service.domain.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import nnt_data.customer_service.infrastructure.persistence.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportingService {

    WebClient webClientBankAccount = WebClient.create("http://localhost:8080");
    WebClient webClientCredit = WebClient.create("http://localhost:8081");
    private final CustomerRepository customerRepository;

    private final CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .build();

    private final CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
    private final CircuitBreaker bankAccountCircuitBreaker = circuitBreakerRegistry.circuitBreaker("bankAccountService");
    private final CircuitBreaker creditCircuitBreaker = circuitBreakerRegistry.circuitBreaker("creditService");

    public Mono<Map<String, Object>> getReportingForProduct(String productId, Date startDate, Date endDate) {
        Mono<Object> bankAccountReportMono = webClientBankAccount.post()
                .uri("/accounts/reporting/salarySummaryForPeriod")
                .body(Mono.just(createRequestBody(productId, startDate, endDate)), Map.class)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(2)) // Timeout de 2 segundos
                .transform(CircuitBreakerOperator.of(bankAccountCircuitBreaker)) // Aplicar circuit breaker
                .onErrorResume(e -> Mono.just(new HashMap<String, Object>() {{
                    put("error", "Error fetching bank account report: " + e.getMessage());
                }}));

        Mono<Object> creditReportMono = webClientCredit.post()
                .uri("/reporting/salarySummaryForPeriod")
                .body(Mono.just(createRequestBodyCredit(productId, startDate, endDate)), Map.class)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(2)) // Timeout de 2 segundos
                .transform(CircuitBreakerOperator.of(creditCircuitBreaker)) // Aplicar circuit breaker
                .onErrorResume(e -> Mono.just(new HashMap<String, Object>() {{
                    put("error", "Error fetching credit report: " + e.getMessage());
                }}));

        return Mono.zip(bankAccountReportMono, creditReportMono)
                .flatMap(tuple -> {
                    Object bankAccountReport = tuple.getT1();
                    Object creditReport = tuple.getT2();

                    Map<String, Object> combinedReport = new HashMap<>();

                    boolean bankAccountError = bankAccountReport instanceof Map && ((Map<?, ?>) bankAccountReport).containsKey("error");
                    boolean creditReportError = creditReport instanceof Map && ((Map<?, ?>) creditReport).containsKey("error");

                    if (bankAccountError && creditReportError) {
                        return Mono.error(new Exception("Failed to fetch both bank account and credit reports. Bank error: " + ((Map<?, ?>) bankAccountReport).get("error") + ", Credit error: " + ((Map<?, ?>) creditReport).get("error")));
                    } else {
                        combinedReport.put("bankAccountReport", bankAccountError ? null : bankAccountReport);
                        combinedReport.put("creditReport", creditReportError ? null : creditReport);
                        return Mono.just(combinedReport);
                    }
                });
    }

    private Map<String, Object> createRequestBody(String productId, Date startDate, Date endDate) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountId", productId);
        requestBody.put("startDate", startDate);
        requestBody.put("endDate", endDate);
        System.out.println(requestBody);
        return requestBody;
    }

    private Map<String, Object> createRequestBodyCredit(String productId, Date startDate, Date endDate) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("creditId", productId);
        requestBody.put("startDate", startDate);
        requestBody.put("endDate", endDate);
        return requestBody;
    }

}
