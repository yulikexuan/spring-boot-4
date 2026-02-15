//: guru.retry.service.RetryableService.java

package guru.retry.service;


import java.time.Duration;
import java.util.UUID;

import guru.retry.domain.model.RetryConfigPropertiesSpec;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Service;


public interface RetryableService {

    String tryFetchDataById(UUID key);

    static RetryTemplate retryTemplate(
            @NonNull RetryConfigPropertiesSpec retryConfigProperties) {

        var retryPolicy = RetryPolicy.builder()
                .includes(RuntimeException.class)
                .maxRetries(retryConfigProperties.maxAttempts())
                .delay(Duration.ofMillis(retryConfigProperties.initialDelayMillis()))
                .multiplier(retryConfigProperties.multiplier())
                .maxDelay(Duration.ofMillis(retryConfigProperties.maxDelayMillis()))
                .jitter(Duration.ofMillis(retryConfigProperties.jitterMillis()))
                .build();

        return new RetryTemplate(retryPolicy);
    }
}


@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
class RetryableServiceImpl implements RetryableService {

    private final RetryTemplate retryTemplate;

    @Override
    public String tryFetchDataById(@NonNull UUID key) {

        try {
            return retryTemplate.execute(() -> this.fetchDataById(key));
        } catch (RetryException e) {
            return "If I am not meant to have it, Lord, please remove the desire from my heart to want it, and help me find peace in its absence.";
        }
    }

    private String fetchDataById(UUID key) {
        throw new RuntimeException(">>> Failed to fetch data by key %s".formatted(key));
    }

}
