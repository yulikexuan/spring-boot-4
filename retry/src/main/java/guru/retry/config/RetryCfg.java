//: guru.retry.config.RetryCfg.java

package guru.retry.config;


import static guru.retry.service.RetryableService.retryTemplate;

import guru.retry.domain.model.RetryConfigPropertiesSpec;
import guru.retry.domain.model.RetryConfigPropertiesSpec.RetryConfigProperties;
import guru.retry.service.RetryableServiceListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.resilience.annotation.EnableResilientMethods;


@Configuration
@EnableResilientMethods
@RequiredArgsConstructor
@EnableConfigurationProperties({ RetryConfigProperties.class })
class RetryCfg {

    private final RetryConfigPropertiesSpec retryConfigProperties;

    @Bean
    RetryTemplate testRetryTemplate(RetryableServiceListener listener) {
        var template = retryTemplate(retryConfigProperties);
        template.setRetryListener(listener);
        return template;
    }

} /// :~