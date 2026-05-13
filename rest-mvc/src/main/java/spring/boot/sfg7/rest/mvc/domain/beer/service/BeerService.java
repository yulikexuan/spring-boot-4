//: spring.boot.sfg7.rest.mvc.domain.beer.service.BeerService.java

package spring.boot.sfg7.rest.mvc.domain.beer.service;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.rest.mvc.domain.beer.dto.BeerDto;
import spring.boot.sfg7.rest.mvc.domain.beer.dto.BeerMapper;
import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;
import spring.boot.sfg7.rest.mvc.domain.beer.repository.BeerRepository;
import spring.boot.sfg7.rest.mvc.domain.service.NotFoundException;


public interface BeerService {

    BeerDto saveNewBeer(BeerDto beer);

    List<BeerDto> findAllBeers();

    BeerDto getBeerById(UUID id);

    void updateBeerById(UUID beerId, BeerDto beer);

    void deleteBeerById(UUID beerId);

    void patchBeerById(UUID beerId, BeerDto beer);
}


@Service
@NullMarked
@RequiredArgsConstructor
class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public BeerDto saveNewBeer(@NonNull BeerDto beer) {

        var data = Beer.builder()
                .version(beer.version())
                .beerName(beer.beerName())
                .beerStyle(beer.beerStyle())
                .upc(beer.upc())
                .quantityOnHand(beer.quantityOnHand())
                .price(beer.price())
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        return beerMapper.toDto(beerRepository.save(data));
    }

    @Override
    public List<BeerDto> findAllBeers() {
        return beerRepository.findAll().stream()
                .map(beerMapper::toDto)
                .toList();
    }

    @Override
    public BeerDto getBeerById(@NonNull UUID id) {
        return beerRepository.findById(id)
                .map(beerMapper::toDto)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public void updateBeerById(UUID beerId, BeerDto beer) {

        var existingBeer = beerRepository.findById(beerId)
                .orElseThrow(() -> new NotFoundException(beerId));

        var incoming = beerMapper.toEntity(beer);
        var newBeer = existingBeer.updateWith(beerId, incoming);

        beerRepository.save(newBeer);
    }

    @Override
    public void patchBeerById(UUID beerId, BeerDto beer) {

        var existingBeer = beerRepository.findById(beerId)
                .orElseThrow(() -> new NotFoundException(beerId));

        var incoming = beerMapper.toEntity(beer);
        var newBeer = existingBeer.patchWith(beerId, incoming);

        beerRepository.save(newBeer);
    }

    @Override
    public void deleteBeerById(UUID beerId) {

        beerRepository.deleteById(beerId);
    }
}
