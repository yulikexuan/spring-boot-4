//: spring.boot.sfg7.rest.mvc.domain.beer.repository.BeerRepositoryTest.java

package spring.boot.sfg7.rest.mvc.domain.beer.repository;


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
import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;
import spring.boot.sfg7.rest.mvc.domain.beer.model.BeerStyle;


@NullMarked
@DataJdbcTest
@AutoConfigureEmbeddedDatabase(provider = EMBEDDED)
@Sql(scripts = "classpath:schema_renew.sql", executionPhase = BEFORE_TEST_METHOD)
@DisplayName("Test BeerRepository - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BeerRepositoryTest {

    @Autowired
    private BeerRepository beerRepository;

    @Test
    void save_new_beer_assigns_uuid_and_round_trips() {
        Instant now = Instant.now();
        Beer beer = Beer.builder()
                .beerName("Galaxy IPA")
                .beerStyle(BeerStyle.IPA)
                .upc("0631234200036")
                .quantityOnHand(122)
                .price(1299)
                .createdDate(now)
                .updateDate(now)
                .build();

        Beer saved = beerRepository.save(beer);

        assertThat(saved.id()).as("DB default gen_random_uuid() fires when id is null").isNotNull();

        Optional<Beer> readBack = beerRepository.findById(saved.id());
        assertThat(readBack).isPresent();
        readBack.ifPresent(b -> {
            assertThat(b.beerName()).isEqualTo("Galaxy IPA");
            assertThat(b.beerStyle()).isEqualTo(BeerStyle.IPA);
            assertThat(b.upc()).isEqualTo("0631234200036");
            assertThat(b.quantityOnHand()).isEqualTo(122);
            assertThat(b.price()).isEqualTo(1299);
            assertThat(b.createdDate()).isCloseTo(now, within(1, ChronoUnit.MILLIS));
            assertThat(b.updateDate()).isCloseTo(now, within(1, ChronoUnit.MILLIS));
        });
    }

    @Test
    void find_all_returns_persisted_rows() {
        assertThat(beerRepository.findAll()).isEmpty();

        beerRepository.save(newBeer("Mango Bobs", BeerStyle.ALE, "0631234200036"));
        beerRepository.save(newBeer("Crank", BeerStyle.PALE_ALE, "0083783375213"));

        List<Beer> all = beerRepository.findAll();

        assertThat(all).hasSize(2)
                .extracting(Beer::beerName)
                .containsExactlyInAnyOrder("Mango Bobs", "Crank");
    }

    @Test
    void find_by_id_returns_existing() {
        Beer saved = beerRepository.save(newBeer("Sunshine", BeerStyle.LAGER, "012345"));

        Optional<Beer> found = beerRepository.findById(saved.id());

        assertThat(found).isPresent();
        found.ifPresent(b -> assertThat(b.beerName()).isEqualTo("Sunshine"));
    }

    @Test
    void find_by_id_returns_empty_for_unknown_uuid() {
        assertThat(beerRepository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void save_existing_id_updates_in_place_without_duplicating() {
        Beer saved = beerRepository.save(newBeer("Old Name", BeerStyle.STOUT, "111"));

        Beer toUpdate = Beer.builder()
                .id(saved.id())
                .version(saved.version())
                .beerName("New Name")
                .beerStyle(BeerStyle.PORTER)
                .upc("111")
                .quantityOnHand(7)
                .price(799)
                .createdDate(saved.createdDate())
                .updateDate(Instant.now())
                .build();

        beerRepository.save(toUpdate);

        assertThat(beerRepository.findAll()).hasSize(1);
        Optional<Beer> readBack = beerRepository.findById(saved.id());
        assertThat(readBack).isPresent();
        readBack.ifPresent(b -> {
            assertThat(b.beerName()).isEqualTo("New Name");
            assertThat(b.beerStyle()).isEqualTo(BeerStyle.PORTER);
        });
    }

    @Test
    void save_with_explicit_unknown_uuid_pins_repository_contract() {
        UUID explicitId = UUID.randomUUID();
        Beer beer = Beer.builder()
                .id(explicitId)
                .beerName("Explicit ID Beer")
                .beerStyle(BeerStyle.PILSNER)
                .upc("999")
                .quantityOnHand(1)
                .price(499)
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        // Spring Data JDBC treats a non-null @Id as "existing entity" and routes save() to UPDATE.
        // With no matching row, 0 rows are affected silently — no insert, no exception.
        Beer saved = beerRepository.save(beer);

        assertThat(saved.id()).isEqualTo(explicitId);
        assertThat(beerRepository.findAll()).isEmpty();
        assertThat(beerRepository.findById(explicitId)).isEmpty();
    }

    @Test
    void delete_by_id_removes_and_subsequent_find_returns_empty() {
        Beer saved = beerRepository.save(newBeer("To Delete", BeerStyle.WHEAT, "222"));

        beerRepository.deleteById(saved.id());

        assertThat(beerRepository.findById(saved.id())).isEmpty();
        assertThat(beerRepository.findAll()).isEmpty();

        assertThatCode(() -> beerRepository.deleteById(UUID.randomUUID()))
                .doesNotThrowAnyException();
    }

    private static Beer newBeer(String name, BeerStyle style, String upc) {
        Instant now = Instant.now();
        return Beer.builder()
                .beerName(name)
                .beerStyle(style)
                .upc(upc)
                .quantityOnHand(1)
                .price(100)
                .createdDate(now)
                .updateDate(now)
                .build();
    }
}
