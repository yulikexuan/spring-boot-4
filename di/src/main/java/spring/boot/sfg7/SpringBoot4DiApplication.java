package spring.boot.sfg7;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import spring.boot.sfg7.controller.DiController;


@Slf4j
@SpringBootApplication
public class SpringBoot4DiApplication {

    public static void main(String[] args) {

        final ConfigurableApplicationContext ctx =
                SpringApplication.run(SpringBoot4DiApplication.class, args);

        DiController diController = ctx.getBean(DiController.class);

        log.info(">>> In app's main method: {}", diController.greeting());
    }

}
