//: spring.boot.sfg7.rest.mvc.web.controller.BeerController.java

package spring.boot.sfg7.rest.mvc.web.controller;


import java.net.URI;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;
import spring.boot.sfg7.rest.mvc.domain.beer.service.BeerService;
import spring.boot.sfg7.rest.mvc.web.util.WebUtils;


@Slf4j
@RestController
@RequestMapping("/sfg7/api/v1/beer")
@RequiredArgsConstructor
class BeerController {

    private final BeerService beerService;

    @GetMapping
    public List<Beer> listBeers(){
        return beerService.findAllBeers();
    }

    @RequestMapping(value = "{beerId}", method = RequestMethod.GET)
    @GetMapping(value = "{beerId}")
    public Beer getBeerById(@PathVariable UUID beerId){

        log.debug("Get Beer by Id - in controller");

        return beerService.getBeerById(beerId);
    }

    @PostMapping
    public ResponseEntity<URI> saveNewBeer(@RequestBody Beer beer) {

        var newBeer = beerService.saveNewBeer(beer);

        URI location = WebUtils.buildLocation("/{beerId}", newBeer::id);

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{beerId}")
    public ResponseEntity<Void> updateBeer(
            @PathVariable UUID beerId, @RequestBody Beer beer) {

        beerService.updateBeerById(beerId, beer);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{beerId}")
    public ResponseEntity<Void> deleteBeerById(@PathVariable UUID beerId) {

        beerService.deleteBeerById(beerId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{beerId}")
    public ResponseEntity<Void> patchBeer(
            @PathVariable UUID beerId, @RequestBody Beer beer) {

        beerService.patchBeerById(beerId, beer);

        return ResponseEntity.noContent().build();
    }

} /// :~