//: spring.boot.sfg7.rest.mvc.domain.customer.repository.CustomerRepositoryTest.java

package spring.boot.sfg7.rest.mvc.domain.customer.repository;


import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.test.context.jdbc.Sql;
import spring.boot.sfg7.rest.mvc.domain.customer.model.Customer;


@NullMarked
@DataJdbcTest
@AutoConfigureEmbeddedDatabase(provider = EMBEDDED)
@Sql(scripts = "classpath:schema_renew.sql", executionPhase = BEFORE_TEST_METHOD)
@DisplayName("Test CustomerRepository - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void save_new_customer_assigns_uuid_and_round_trips() {
        Instant now = Instant.now();
        Customer customer = Customer.builder()
                .name("Ada Lovelace")
                .createdDate(now)
                .updateDate(now)
                .build();

        Customer saved = customerRepository.save(customer);

        assertThat(saved.id()).as("DB default gen_random_uuid() fires when id is null").isNotNull();

        Optional<Customer> readBack = customerRepository.findById(saved.id());
        assertThat(readBack).isPresent();
        readBack.ifPresent(c -> {
            assertThat(c.name()).isEqualTo("Ada Lovelace");
            assertThat(c.createdDate()).isCloseTo(now, within(1, ChronoUnit.MILLIS));
            assertThat(c.updateDate()).isCloseTo(now, within(1, ChronoUnit.MILLIS));
        });
    }

    @Test
    void find_all_returns_persisted_rows() {
        assertThat(customerRepository.findAll()).isEmpty();

        customerRepository.save(newCustomer("Alice"));
        customerRepository.save(newCustomer("Bob"));

        List<Customer> all = customerRepository.findAll();

        assertThat(all).hasSize(2)
                .extracting(Customer::name)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void find_by_id_returns_existing() {
        Customer saved = customerRepository.save(newCustomer("Carol"));

        Optional<Customer> found = customerRepository.findById(saved.id());

        assertThat(found).isPresent();
        found.ifPresent(c -> assertThat(c.name()).isEqualTo("Carol"));
    }

    @Test
    void find_by_id_returns_empty_for_unknown_uuid() {
        assertThat(customerRepository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void save_existing_id_updates_in_place_without_duplicating() {
        Customer saved = customerRepository.save(newCustomer("Old Name"));

        Customer toUpdate = Customer.builder()
                .id(saved.id())
                .version(saved.version())
                .name("New Name")
                .createdDate(saved.createdDate())
                .updateDate(Instant.now())
                .build();

        customerRepository.save(toUpdate);

        assertThat(customerRepository.findAll()).hasSize(1);
        Optional<Customer> readBack = customerRepository.findById(saved.id());
        assertThat(readBack).isPresent();
        readBack.ifPresent(c -> assertThat(c.name()).isEqualTo("New Name"));
    }

    @Test
    void save_with_explicit_unknown_uuid_pins_repository_contract() {
        UUID explicitId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(explicitId)
                .name("Explicit ID Customer")
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        // Spring Data JDBC treats a non-null @Id as "existing entity" and routes save() to UPDATE.
        // With no matching row, 0 rows are affected silently — no insert, no exception.
        Customer saved = customerRepository.save(customer);

        assertThat(saved.id()).isEqualTo(explicitId);
        assertThat(customerRepository.findAll()).isEmpty();
        assertThat(customerRepository.findById(explicitId)).isEmpty();
    }

    @Test
    void delete_by_id_removes_and_subsequent_find_returns_empty() {
        Customer saved = customerRepository.save(newCustomer("To Delete"));

        customerRepository.deleteById(saved.id());

        assertThat(customerRepository.findById(saved.id())).isEmpty();
        assertThat(customerRepository.findAll()).isEmpty();

        assertThatCode(() -> customerRepository.deleteById(UUID.randomUUID()))
                .doesNotThrowAnyException();
    }

    private static Customer newCustomer(String name) {
        Instant now = Instant.now();
        return Customer.builder()
                .name(name)
                .createdDate(now)
                .updateDate(now)
                .build();
    }
}
