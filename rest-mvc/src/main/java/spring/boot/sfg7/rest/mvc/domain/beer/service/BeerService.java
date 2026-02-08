//: spring.boot.sfg7.rest.mvc.domain.beer.service.BeerService.java

package spring.boot.sfg7.rest.mvc.domain.beer.service;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;
import spring.boot.sfg7.rest.mvc.domain.beer.repository.BeerRepository;


public interface BeerService {

    Beer saveNewBeer(Beer beer);

    List<Beer> findAllBeers();

    Beer getBeerById(UUID id);

    void updateBeerById(UUID beerId, Beer beer);

    void deleteBeerById(UUID beerId);

    void patchBeerById(UUID beerId, Beer beer);
}


@Service
@NullMarked
@RequiredArgsConstructor
class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;

    @Override
    public Beer saveNewBeer(@NonNull Beer beer) {

        var data = Beer.builder()
                .beerName(beer.beerName())
                .version(beer.version())
                .beerStyle(beer.beerStyle())
                .upc(beer.upc())
                .quantityOnHand(beer.quantityOnHand())
                .price(beer.price())
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        return beerRepository.save(data);
    }

    @Override
    public List<Beer> findAllBeers() {
        return beerRepository.findAll();
    }

    @Override
    public Beer getBeerById(@NonNull UUID id) {
        return beerRepository.findById(id).orElseThrow();
    }

    @Override
    public void updateBeerById(UUID beerId, Beer beer) {

        var existingBeer = getBeerById(beerId);
        var newBeer = existingBeer.updateWith(beerId, beer);

        beerRepository.save(newBeer);
    }

    @Override
    public void patchBeerById(UUID beerId, Beer beer) {

        var existingBeer = getBeerById(beerId);
        var newBeer = existingBeer.patchWith(beerId, beer);

        beerRepository.save(newBeer);
    }

    @Override
    public void deleteBeerById(UUID beerId) {

        beerRepository.deleteById(beerId);
    }
}