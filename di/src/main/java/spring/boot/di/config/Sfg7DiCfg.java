//: spring.boot.sfg7.config.Sfg7Cfg.java

package spring.boot.di.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import spring.boot.di.service.GreetingService;
import spring.boot.di.service.Presentation;


@Configuration
public class Sfg7DiCfg {

    @Bean // Bean name is this method name by default
    // @Bean(name = "baseGreetingService")
    GreetingService baseGreetingService() {
        return GreetingService.baseGreetingService();
    }

    @Bean
    @Primary
    GreetingService primaryGreetingService() {
        return GreetingService.primaryGreetingService();
    }

    // Cannot use the same bean name here, must be different,
    // otherwise, will throw runtime exception
    //    //@Bean("i18NGreetingService")
    //    GreetingService englishGreetingService() {
    //        return GreetingService.englishGreetingService();
    //    }
    //
    //    //@Bean("i18NGreetingService")
    //    GreetingService frenchGreetingService() {
    //        return GreetingService.frenchGreetingService();
    //    }

    @Bean
    Presentation briefPresentation() {
        return Presentation.briefPresentation();
    }

    @Bean
    Presentation fullPresentation() {
        return Presentation.fullPresentation();
    }

} /// :~