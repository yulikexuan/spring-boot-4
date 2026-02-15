//: guru.retry.domain.model.RetryConfigPropertiesSpec.java

package guru.retry.domain.model;


import org.springframework.boot.context.properties.ConfigurationProperties;


public interface RetryConfigPropertiesSpec {

    int maxAttempts();
    long initialDelayMillis();
    int multiplier();
    long maxDelayMillis();
    long jitterMillis();

    @ConfigurationProperties(prefix = "app.retry.test")
    record RetryConfigProperties(
            int maxAttempts,
            long initialDelayMillis,
            int multiplier,
            long maxDelayMillis,
            long jitterMillis)
            implements RetryConfigPropertiesSpec {

    }

}
