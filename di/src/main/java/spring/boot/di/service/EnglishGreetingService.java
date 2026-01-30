//: spring.boot.sfg7.service.EnglishGreetingService.java

package spring.boot.di.service;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Profile({"default", "EN"})
@Service("i18NGreetingService")
class EnglishGreetingService implements GreetingService {

    @Override
    public String greeting() {
        return ENGLISH_GREETING;
    }

} /// :~