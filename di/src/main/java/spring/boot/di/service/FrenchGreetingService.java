//: spring.boot.sfg7.service.FrenchGreetingService.java

package spring.boot.di.service;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Profile("FR")
@Service("i18NGreetingService")
class FrenchGreetingService implements GreetingService {

    @Override
    public String greeting() {
        return FRENCH_GREETING;
    }

} /// :~