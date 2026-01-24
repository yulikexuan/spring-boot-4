package spring.boot.sfg7;


import static org.assertj.core.api.Assertions.assertThat;
import static spring.boot.sfg7.service.GreetingService.BASE_GREETING;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import spring.boot.sfg7.controller.DiController;


@SpringBootTest
class SpringBoot4DiApplicationIT {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private DiController wiredDiController;

    @Test
    void auto_Wired_DiController() {
        assertThat(wiredDiController.greeting()).isEqualTo(BASE_GREETING);
    }

    @Test
    void context_Loads_DiController() {

        // Given
        DiController con = ctx.getBean(DiController.class);

        // When & Then
        assertThat(con.greeting()).isEqualTo(BASE_GREETING);
    }

}
