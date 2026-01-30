//: spring.boot.sfg7.controller.EnvControllerTest.java

package spring.boot.di.controller;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spring.boot.di.controller.EnvController;

import static org.assertj.core.api.Assertions.assertThat;
import static spring.boot.di.service.EnvService.DEV_ENV;
import static spring.boot.di.service.EnvService.QA_ENV;


@SpringBootTest
@DisplayName("Test Profile with EnvController - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class EnvControllerIT {

    @Autowired
    private EnvController envController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void dev_If_The_Default_Env() {
        assertThat(this.envController.environment()).endsWith(DEV_ENV);
    }

    @Nested
    @ActiveProfiles({"dev", "EN"})
    class DevEnvIT {

        @Autowired
        private EnvController envController;

        @Test
        void dev_Env_Is_Active() {
            assertThat(this.envController.environment()).endsWith(DEV_ENV);
        }

    }

    @Nested
    @ActiveProfiles({"qa", "EN"})
    class QaEnvIT {

        @Autowired
        private EnvController envController;

        @Test
        void dev_Env_Is_Active() {
            assertThat(this.envController.environment()).endsWith(QA_ENV);
        }

    }

}
