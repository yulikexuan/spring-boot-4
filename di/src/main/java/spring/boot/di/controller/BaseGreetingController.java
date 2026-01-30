//: spring.boot.sfg7.controller.BaseGreetingController.java

package spring.boot.di.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import spring.boot.di.service.GreetingService;
import spring.boot.di.service.Presentation;


@Slf4j
@Controller
/*
 * With Lombok's @RequiredArgsConstructor, Spring needs the @Qualifier on the
 * constructor parameter, but since Lombok generates the constructor,
 * you need to use a different approach.
 * Change the controller to use either field injection or explicit constructor.
 * Use explicit constructor is preferred.
 */
// @Qualifier("baseGreetingService") @RequiredArgsConstructor
public class BaseGreetingController {

    private final GreetingService baseGreetingService;
    private final Presentation presentation;

    public BaseGreetingController(
            // Used to wired a bean which is not annotated as `Primary` bean
            // it also means there is a primary bean existing already
            @Qualifier("baseGreetingService")
            GreetingService baseGreetingService,
            // wired with bean `briefPresentation`
            Presentation briefPresentation) {

        this.baseGreetingService = baseGreetingService;
        this.presentation = briefPresentation;
    }

    public String greeting() {

        log.info(">>> Inside {}", this.getClass().getSimpleName());

        return this.baseGreetingService.greeting();
    }

    public String presenting() {
        return this.presentation.present();
    }

} /// :~