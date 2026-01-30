//: spring.boot.sfg7.controller.I18NController.java

package spring.boot.di.controller;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import spring.boot.di.service.GreetingService;


@Controller
public class I18NController {

    private final GreetingService greetingService;

    public I18NController(
            @Qualifier("i18NGreetingService")
            GreetingService greetingService) {

        this.greetingService = greetingService;
    }

    public String sayHello() {
        return greetingService.greeting();
    }

} /// :~