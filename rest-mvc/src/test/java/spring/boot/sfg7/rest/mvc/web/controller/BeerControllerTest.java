//: spring.boot.sfg7.rest.mvc.web.controller.BeerControllerTest.java

package spring.boot.sfg7.rest.mvc.web.controller;


import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
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

        // When
        mockMvc.perform(requestBuilder)
                .andExpect(okMatcher)
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(beerId));
                // JsonPathResultMatchers


    }

}
