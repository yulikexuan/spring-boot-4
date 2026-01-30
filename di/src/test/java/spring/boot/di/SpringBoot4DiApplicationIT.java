package spring.boot.di;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static spring.boot.di.service.GreetingService.BASE_GREETING;
import static spring.boot.di.service.GreetingService.FRENCH_GREETING;
import static spring.boot.di.service.GreetingService.PRIMARY_GREETING;
import static spring.boot.di.service.Presentation.BRIEFLY_PRESENTING;
import static spring.boot.di.service.Presentation.FULLY_PRESENTING;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import spring.boot.di.controller.BaseGreetingController;
import spring.boot.di.controller.DiController;
import spring.boot.di.controller.I18NController;


@ActiveProfiles({"FR", "dev"})
@SpringBootTest
class SpringBoot4DiApplicationIT {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private DiController wiredDiController;

    @Autowired
    private BaseGreetingController wiredBaseGreetingController;

    @Autowired
    private I18NController wiredI18NController;

    @Test
    void context_Loads_DiController() {

        // Given
        DiController con = ctx.getBean(DiController.class);

        // When & Then
        assertAll(() -> {
            assertThat(con.greeting()).isEqualTo(PRIMARY_GREETING);
            assertThat(wiredDiController.greeting()).isEqualTo(PRIMARY_GREETING);
            assertThat(wiredBaseGreetingController.greeting()).isEqualTo(BASE_GREETING);
            assertThat(wiredDiController.presenting()).contains(FULLY_PRESENTING);
            assertThat(wiredBaseGreetingController.presenting()).contains(BRIEFLY_PRESENTING);
            assertThat(wiredI18NController.sayHello()).contains(FRENCH_GREETING);
        });
    }

}
