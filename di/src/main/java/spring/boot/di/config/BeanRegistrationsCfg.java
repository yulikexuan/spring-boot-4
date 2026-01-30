//: spring.boot.di.config.BeanRegistrationsCfg.java

package spring.boot.di.config;


import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import spring.boot.di.service.WelcomeService;


@Configuration
@Import(BeanRegistrationsCfg.WelcomeBeanRegistrar.class)
class BeanRegistrationsCfg {

    static class WelcomeBeanRegistrar implements BeanRegistrar {

        @Override
        public void register(BeanRegistry registry, Environment env) {

            registry.registerBean(WelcomeService.class);

            registry.registerBean(
                    "welcomeService2",
                    WelcomeService.class,
                    spec -> spec
                    .prototype()
                    .lazyInit()
                    .primary());
        }
    }

} /// :~