//: spring.boot.sfg7.rest.mvc.web.controller.BeerControllerTest.java

package spring.boot.sfg7.rest.mvc.web.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.boot.sfg7.rest.mvc.web.controller.BeerController.BEER_PATH;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;
import spring.boot.sfg7.rest.mvc.domain.beer.model.BeerStyle;
import spring.boot.sfg7.rest.mvc.domain.beer.service.BeerService;
import tools.jackson.databind.ObjectMapper;


@WebMvcTest(BeerController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Test BeerController - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BeerControllerTest {

    static final String BEER_URI = BEER_PATH;
    public static final String BEER_ID_PATH = "/{beerId}";

    @MockitoBean
    private BeerService beerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    private ArgumentCaptor<Beer> beerArgumentCaptor;

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

    @Test
    void able_To_Create_New_Beer() throws Exception {

        // Given
        Beer requestBeer = Beer.builder()
                .beerName("New Brew")
                .beerStyle(BeerStyle.IPA)
                .upc("99999")
                .quantityOnHand(50)
                .price(1499)
                .build();

        UUID newBeerId = UUID.randomUUID();
        Beer savedBeer = Beer.builder()
                .id(newBeerId)
                .beerName(requestBeer.beerName())
                .beerStyle(requestBeer.beerStyle())
                .upc(requestBeer.upc())
                .quantityOnHand(requestBeer.quantityOnHand())
                .price(requestBeer.price())
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        given(beerService.saveNewBeer(any(Beer.class))).willReturn(savedBeer);

        RequestBuilder postBuilder = MockMvcRequestBuilders.post(BEER_URI)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBeer));

        ResultMatcher createdMatcher = status().isCreated();
        ResultMatcher locationHeaderMatcher = header()
                .string(HttpHeaders.LOCATION, containsString(newBeerId.toString()));

        // When
        mockMvc.perform(postBuilder)
                .andExpect(createdMatcher)
                .andExpect(locationHeaderMatcher);

        // Then
        then(beerService).should().saveNewBeer(requestBeer);
    }

    @Test
    void able_To_Update_Existing_Beer() throws Exception {

        // Given
        UUID beerId = UUID.randomUUID();

        Beer beer = Beer.builder()
                .id(beerId)
                .beerName("Nice Beer")
                .beerStyle(BeerStyle.LAGER)
                .upc("12345")
                .quantityOnHand(100)
                .price(1260)
                .build();

        URI updateUri = ServletUriComponentsBuilder.fromUriString(BEER_URI)
                .path(BEER_ID_PATH)
                .buildAndExpand(beerId)
                .toUri();

        RequestBuilder putBuilder = MockMvcRequestBuilders.put(updateUri)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beer));

        ResultMatcher noContentMatcher = status().isNoContent();

        // When
        mockMvc.perform(putBuilder).andExpect(noContentMatcher);

        // Then
        then(beerService).should().updateBeerById(beerId, beer);
    }

    @Test
    void able_To_Delete_Beer_By_Id() throws Exception {

        // Given
        UUID beerId = UUID.randomUUID();

        URI deleteUri = ServletUriComponentsBuilder.fromUriString(BEER_URI)
                .path(BEER_ID_PATH)
                .buildAndExpand(beerId)
                .toUri();

        RequestBuilder deleteBuilder = MockMvcRequestBuilders.delete(deleteUri)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON);

        ResultMatcher noContentMatcher = status().isNoContent();

        // When
        mockMvc.perform(deleteBuilder).andExpect(noContentMatcher);

        // Then
        then(beerService).should().deleteBeerById(uuidArgumentCaptor.capture());
        assertThat(beerId).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    void able_To_Patch_Beer_By_Id() throws Exception {

        // Given
        UUID beerId = UUID.randomUUID();

        URI patchUri = ServletUriComponentsBuilder.fromUriString(BEER_URI)
                .path(BEER_ID_PATH)
                .buildAndExpand(beerId)
                .toUri();

        String newName = "The Best Beer";
        Map<String, Object> patch = Map.of("beerName", newName);
        String payload = objectMapper.writeValueAsString(patch);

        RequestBuilder patchBuilder = MockMvcRequestBuilders.patch(patchUri)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(payload);

        ResultMatcher noContentMatcher = status().isNoContent();

        // When
        mockMvc.perform(patchBuilder).andExpect(noContentMatcher);

        // Then
        then(beerService).should().patchBeerById(
                uuidArgumentCaptor.capture(),
                beerArgumentCaptor.capture());

        assertThat(beerId).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beerArgumentCaptor.getValue().beerName()).isEqualTo(newName);
    }

}
