//: spring.boot.sfg7.controller.EnvController.java

package spring.boot.di.controller;


import org.springframework.stereotype.Controller;
import spring.boot.di.service.EnvService;


@Controller
public class EnvController {

    private final EnvService envService;

    public EnvController(EnvService envService) {
        this.envService = envService;
    }

    public String environment() {
        return ">>> Running in " + envService.environment();
    }

} /// :~