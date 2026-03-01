//: spring.boot.sfg7.rest.mvc.web.controller.BeerControllerTest.java

package spring.boot.sfg7.rest.mvc.web.controller;


import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;
import spring.boot.sfg7.rest.mvc.domain.beer.model.BeerStyle;
import spring.boot.sfg7.rest.mvc.domain.beer.service.BeerService;


@WebMvcTest(BeerController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Test BeerController - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BeerControllerTest {

    static final String BEER_URI = "/sfg7/api/v1/beer";
    public static final String BEER_ID_PATH = "/{beerId}";

    @MockitoBean
    private BeerService beerService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    private static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    @Test
    void able_To_Get_Beer_Back_By_Beer_Id() throws Exception {

        // Given
        final String beerId = randomUUID();

        Beer testBeer = Beer.builder()
                .id(UUID.fromString(beerId))
                .beerName("Heineken Silver")
                .beerStyle(BeerStyle.LAGER)
                .upc("34567")
                .quantityOnHand(100)
                .price(1260)
                .build();

        given(beerService.getBeerById(UUID.fromString(beerId)))
                .willReturn(testBeer);

        final URI beerIdUri = ServletUriComponentsBuilder.fromUriString(BEER_URI)
                .path(BEER_ID_PATH)
                .buildAndExpand(beerId)
                .toUri();

        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(beerIdUri)
                .accept(APPLICATION_JSON);

        final ResultMatcher okMatcher = status().isOk();
        final ResultMatcher contentMatcher = content().contentType(APPLICATION_JSON);

        // When
        mockMvc.perform(requestBuilder)
                .andExpect(okMatcher)
                .andExpect(contentMatcher)
                .andExpect(jsonPath("$.id").value(beerId))
                .andExpect(jsonPath("$.beerName").value("Heineken Silver"))
                .andExpect(jsonPath("$.beerStyle").value("LAGER"))
                .andExpect(jsonPath("$.upc").value("34567"))
                .andExpect(jsonPath("$.quantityOnHand").value(100))
                .andExpect(jsonPath("$.price").value(1260));
    }

    @Test
    void able_To_Get_All_Beers_In_List() throws Exception {

        // Given
        List<Beer> beers = List.of(
                Beer.builder()
                        .id(UUID.fromString(randomUUID()))
                        .version(1)
                        .beerName("Galaxy Cat")
                        .beerStyle(BeerStyle.PALE_ALE)
                        .upc("12356")
                        .price(1299)
                        .quantityOnHand(122)
                        .createdDate(Instant.now())
                        .updateDate(Instant.now())
                        .build(),
                Beer.builder()
                        .id(UUID.fromString(randomUUID()))
                        .version(1)
                        .beerName("Crank")
                        .beerStyle(BeerStyle.PALE_ALE)
                        .upc("12356222")
                        .price(1199)
                        .quantityOnHand(392)
                        .createdDate(Instant.now())
                        .updateDate(Instant.now())
                        .build());

        given(beerService.findAllBeers()).willReturn(beers);

        URI url = ServletUriComponentsBuilder.fromUriString(BEER_URI)
                .build()
                .toUri();

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(url)
                .accept(APPLICATION_JSON);

        ResultMatcher okMatcher = status().isOk();
        ResultMatcher contentMatcher = content().contentType(APPLICATION_JSON);

        // When
        mockMvc.perform(requestBuilder)
                .andExpect(okMatcher)
                .andExpect(contentMatcher)
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(beers.size()));
    }

}
