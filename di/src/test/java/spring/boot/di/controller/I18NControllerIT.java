//: spring.boot.sfg7.controller.I18NControllerIT.java

package spring.boot.di.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static spring.boot.di.service.GreetingService.ENGLISH_GREETING;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.boot.di.controller.I18NController;


@SpringBootTest
@DisplayName("Test Default Profile - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class I18NControllerIT {

    @Autowired
    private I18NController i18NController;

    @Test
    void english_Greeting_Is_Default() {
        assertThat(i18NController.sayHello()).isEqualTo(ENGLISH_GREETING);
    }

}
