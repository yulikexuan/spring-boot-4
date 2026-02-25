//: spring.boot.di.domain.model.jdk25.lambda.RunnableVsCallableTest.java

package spring.boot.di.domain.model.jdk25.lambda;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Test Runnable and Callable Class - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class RunnableVsCallableTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void common_Thread_Pool_Executor_Of_ForkJoinPool_Block_Main_Thead() {

        // Given

        // When
        var result = RunnableVsCallable.runAtomicNumbersInParallel();
        int monkey2 = Integer.parseInt(result.substring(4));

        // Then
        assertThat(result).startsWith("100 ");
        assertThat(monkey2).isLessThan(100);
    }

}
