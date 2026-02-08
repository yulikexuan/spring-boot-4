//: spring.boot.sfg7.rest.mvc.bootstrap.BootstrapData.java

package spring.boot.sfg7.rest.mvc.bootstrap;


import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;
import spring.boot.sfg7.rest.mvc.domain.beer.model.BeerStyle;
import spring.boot.sfg7.rest.mvc.domain.beer.service.BeerService;
import spring.boot.sfg7.rest.mvc.domain.customer.model.Customer;
import spring.boot.sfg7.rest.mvc.domain.customer.service.CustomerService;


@Slf4j
@Service
@RequiredArgsConstructor
class BootstrapData implements CommandLineRunner {

    private final BeerService beerService;
    private final CustomerService customerService;

    @Override
    public void run(String... args) throws Exception {

        Beer beer1 = Beer.builder()
                .version(1)
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(1299)
                .quantityOnHand(122)
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        beer1 = beerService.saveNewBeer(beer1);

        Beer beer2 = Beer.builder()
                .version(1)
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(1199)
                .quantityOnHand(392)
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        beer2 = beerService.saveNewBeer(beer2);

        Beer beer3 = Beer.builder()
                .version(1)
                .beerName("Sunshine City")
                .beerStyle(BeerStyle.IPA)
                .upc("12356")
                .price(1399)
                .quantityOnHand(144)
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        beer3 = beerService.saveNewBeer(beer3);

        var beerCount = Stream.of(beer1, beer2, beer3)
                .filter(b -> b.id() != null)
                .count();

        log.info(">>> {} different Beers saved.", beerCount);

        Customer c1 = Customer.builder()
                .version(1)
                .name("Fido")
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();
        Customer c2 = Customer.builder()
                .version(1)
                .name("Koodo")
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();
        Customer c3 = Customer.builder()
                .version(1)
                .name("Bell")
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        List<Customer> customers = List.of(
            customerService.saveNewCustomer(c1),
            customerService.saveNewCustomer(c3),
            customerService.saveNewCustomer(c2));

        var numOfSavedCustomers = customers.stream()
                .filter(c -> c.id() != null)
                .count();

        log.info(">>> {} different Customer saved.", numOfSavedCustomers);
    }

} /// :~