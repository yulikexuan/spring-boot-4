//: spring.boot.sfg7.data.domain.optics.traversals.CourseTest.java

package spring.boot.sfg7.data.domain.optics.traversals;


import org.higherkindedj.optics.Lens;
import org.higherkindedj.optics.Traversal;
import org.higherkindedj.optics.util.Traversals;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


@ExtendWith(MockitoExtension.class)
@DisplayName("Test Optics Traversals with Course Class - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CourseTest {

    private Course course;

    @BeforeEach
    void setUp() {
    }

    @Nested
    class BasicsOfTraversalsTest {

        private List<String> names;

        private Traversal<List<String>, String> nameTraversal;

        @BeforeEach
        void setUp() {
            names = List.of("Alice", "Bob", "Charlie");
            nameTraversal = Traversals.forList();
        }

        @Test
        void traversal_Is_Able_To_Modify_List_Easily() {

            // Given

            // When
            var newNames = Traversals.modify(
                    nameTraversal, String::toUpperCase, names);

            // Then
            assertThat(newNames).isNotSameAs(names);
            assertThat(newNames).containsExactly("ALICE", "BOB", "CHARLIE");
        }

        @Test
        void traversals_Can_Fetch_All_Elements_From_List() {

            // Given

            // When
            var allNames = Traversals.getAll(nameTraversal, names);

            // Then
            assertThat(allNames).containsExactly("Alice", "Bob", "Charlie");
        }

    }

    @Nested
    class CompositedTraversalTest {

        private Profile profile_0;
        private Profile profile_1;
        private Profile profile_2;

        private Student student_0;
        private Student student_1;
        private Student student_2;

        private Course course;

        private Lens<Course, List<Student>> studentsLens;
        private Traversal<List<Student>, Student> eachStudentTraversal;
        private Lens<Student, Profile> profileLens;
        private Lens<Profile, Integer> ageLens;

        private Traversal<Course, Integer> allStudentAges;

        @BeforeEach
        void setUp() {

            profile_0 = new Profile(17, Gender.FEMALE);
            profile_1 = new Profile(18, Gender.MALE);
            profile_2 = new Profile(16, Gender.MALE);

            student_0 = new Student("Alice", profile_0, 85);
            student_1 = new Student("Bob", profile_1, 90);
            student_2 = new Student("Charlie", profile_2, 58);

            course = new Course("Math", List.of(student_0, student_1, student_2));

            studentsLens = CourseLenses.students();
            eachStudentTraversal = Traversals.forList();
            profileLens = StudentLenses.profile();
            ageLens = ProfileLenses.age();

            allStudentAges = studentsLens.asTraversal()
                    .andThen(eachStudentTraversal)
                    .andThen(profileLens.asTraversal())
                    .andThen(ageLens.asTraversal());
        }

        @Test
        void able_To_Compose_Traversal_With_Lenses_To_Process_Deeper_Data_Of_Collection_Elements() {

            // Given

            // When
            var updatedCourse = Traversals.modify(
                    allStudentAges, a -> a + 1, course);

            var updatedAges = Traversals.getAll(allStudentAges, updatedCourse);

            // Then
            assertThat(updatedCourse).isNotSameAs(course);
            assertThat(updatedAges).containsExactly(18, 19, 17);
        }

        @Test
        void able_To_Set_Up_Filters_In_Traversals() {

            // Given

            // When
            Traversal<Course, Integer> minorAge =
                    allStudentAges.filtered(a -> a < 18);

            var minors = Traversals.getAll(minorAge, course);

            // Then
            assertThat(minors).containsExactly(17, 16);

            // Given
        }

    }

}
