//: guru.modulith.ModulithApplicationTest.java

package guru.modulith;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


@ExtendWith(MockitoExtension.class)
@DisplayName("Test ModulithApplication Class - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ModulithApplicationTest {

    private ApplicationModules applicationModules;

    @BeforeEach
    void setUp() {
        applicationModules = ApplicationModules.of(ModulithApplication.class);
        applicationModules.verify();
    }

    @Test
    void contextLoads() {

        // Given

        // When
        List<String> moduleNames = this.applicationModules.stream()
                .map(ApplicationModule::getName)
                .toList();

        // Then
        assertThat(moduleNames).hasSize(2)
                .containsExactly("notification", "product");
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(applicationModules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}
