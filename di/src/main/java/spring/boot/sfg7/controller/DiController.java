//: spring.boot.sfg7.controller.DiController.java

package spring.boot.sfg7.controller;


import static spring.boot.sfg7.service.GreetingService.baseGreetingService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import spring.boot.sfg7.service.GreetingService;


@Slf4j
@Controller
public class DiController {

    private final GreetingService greetingService;

    public DiController() {
        this.greetingService = baseGreetingService();
    }

    public String greeting() {

        log.info(">>> Inside DiController");

        return this.greetingService.greeting();
    }

} /// :~