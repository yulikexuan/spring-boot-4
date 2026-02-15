//: guru.retry.service.RetryableServiceListener.java

package guru.retry.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryState;
import org.springframework.core.retry.Retryable;
import org.springframework.stereotype.Component;


public interface RetryableServiceListener extends RetryListener {

}


@Slf4j
@Component
class RetryableServiceListenerImpl implements RetryableServiceListener {

    @Override
    public void onRetryableExecution(RetryPolicy retryPolicy,
            Retryable<?> retryable, RetryState retryState) {

        log.info(">>> RetryableService retry-attempt {}", retryState.getRetryCount());
    }

    @Override
    public void onRetryPolicyExhaustion(
            RetryPolicy retryPolicy,
            Retryable<?> retryable,
            RetryException exception) {

        log.warn(">>> Retry Max-Attempts Exhausted!");
    }
}