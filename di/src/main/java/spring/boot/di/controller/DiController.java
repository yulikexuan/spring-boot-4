//: spring.boot.sfg7.controller.DiController.java

package spring.boot.di.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import spring.boot.di.service.GreetingService;
import spring.boot.di.service.Presentation;


@Slf4j
@Controller
@RequiredArgsConstructor
public class DiController {

    private final GreetingService greetingService;
    private final Presentation fullPresentation; // wired with bean `fullPresentation`

    public String greeting() {

        log.info(">>> Inside DiController");

        return this.greetingService.greeting();
    }

    public String presenting() {

        return fullPresentation.present();
    }

    public void beforeInit(){
        System.out.println("## - Before Init - Called by Bean Post Processor");
    }

    public void afterInit(){
        System.out.println("## - After init called by Bean Post Processor");
    }

} /// :~