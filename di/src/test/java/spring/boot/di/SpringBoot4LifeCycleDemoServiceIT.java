//: spring.boot.sfg7.SpringBoot4LifeCycleDemoServiceIT.java

package spring.boot.di;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles({"EN", "dev"})
class SpringBoot4LifeCycleDemoServiceIT {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void validate_Bean_Life_Cycle_Activities() {
        assertThat(ctx).isNotNull();
    }

} /// :~