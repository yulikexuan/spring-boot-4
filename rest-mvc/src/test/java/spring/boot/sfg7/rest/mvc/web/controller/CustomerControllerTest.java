//: spring.boot.sfg7.rest.mvc.web.controller.CustomerControllerTest.java

package spring.boot.sfg7.rest.mvc.web.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.boot.sfg7.rest.mvc.web.controller.CustomerController.CUSTOMER_PATH;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import spring.boot.sfg7.rest.mvc.domain.customer.model.Customer;
import spring.boot.sfg7.rest.mvc.domain.customer.service.CustomerService;
import spring.boot.sfg7.rest.mvc.domain.service.NotFoundException;
import tools.jackson.databind.ObjectMapper;


@WebMvcTest(CustomerController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Test CustomerController - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CustomerControllerTest {

    static final String CUSTOMER_URI = CUSTOMER_PATH;
    public static final String CUSTOMER_ID_PATH = "/{customerId}";

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<UUID> idCaptor;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    private UUID customerId;
    private String name;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        name = RandomStringUtils.insecure().nextAlphanumeric(7);
    }

    @Test
    void able_To_Get_The_Customer_By_Id() throws Exception {

        // Given
        Instant instant = Instant.now();
        Customer customer = Customer.builder()
                .id(customerId)
                .name(name)
                .version(1)
                .createdDate(instant)
                .updateDate(instant)
                .build();

        given(customerService.getCustomerById(customerId)).willReturn(customer);

        URI customerByIdUri = ServletUriComponentsBuilder
                .fromUriString(CUSTOMER_URI)
                .path(CUSTOMER_ID_PATH)
                .buildAndExpand(customerId)
                .toUri();

        RequestBuilder rb = MockMvcRequestBuilders
                .get(customerByIdUri)
                .accept(APPLICATION_JSON);

        ResultMatcher okMatcher = status().isOk();
        ResultMatcher contentMatcher = content().contentType(APPLICATION_JSON);

        // When
        mockMvc.perform(rb)
                .andExpect(okMatcher)
                .andExpect(contentMatcher)
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void customer_Is_Not_Found() throws Exception {

        // Given
        var customerId = UUID.randomUUID();

        given(customerService.getCustomerById(customerId))
                .willThrow(NotFoundException.class);

        final URI customerIdUri = ServletUriComponentsBuilder
                .fromUriString(CUSTOMER_URI)
                .path(CUSTOMER_ID_PATH)
                .buildAndExpand(customerId)
                .toUri();

        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(customerIdUri)
                .accept(APPLICATION_JSON);

        // When & Then
        mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }

    @Test
    void able_To_Create_New_Customer() throws Exception {

        // Given
        Customer newCustomer = Customer.builder()
                .name(name)
                .version(1)
                .build();
        UUID id = UUID.randomUUID();
        Customer savedCustomer = Customer.builder()
                .id(id)
                .name(newCustomer.name())
                .version(newCustomer.version())
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        given(customerService.saveNewCustomer(newCustomer))
                .willReturn(savedCustomer);

        RequestBuilder rb = MockMvcRequestBuilders
                .post(CUSTOMER_URI)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCustomer));

        ResultMatcher createdMatcher = status().isCreated();
        ResultMatcher locationHeaderMatcher = header()
                .string(HttpHeaders.LOCATION, containsString(id.toString()));

        // When
        mockMvc.perform(rb)
                .andExpect(createdMatcher)
                .andExpect(locationHeaderMatcher);

        // Then
        then(customerService).should().saveNewCustomer(newCustomer);
    }

    @Test
    void able_To_Update_Customer_By_Id() throws Exception {

        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .name(name)
                .version(1)
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        URI updateUri = ServletUriComponentsBuilder
                .fromUriString(CUSTOMER_URI)
                .path(CUSTOMER_ID_PATH)
                .buildAndExpand(customerId)
                .toUri();

        RequestBuilder rb = MockMvcRequestBuilders.put(updateUri)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer));

        ResultMatcher noContentMatcher = status().isNoContent();

        // When
        mockMvc.perform(rb).andExpect(noContentMatcher);

        // Then
        then(customerService).should().updateCustomerById(customerId, customer);
    }

    @Test
    void able_To_Delete_Customer_By_Id() throws Exception {

        // Given
        UUID customerId = UUID.randomUUID();

        URI deleteUri = ServletUriComponentsBuilder.fromUriString(CUSTOMER_URI)
                .path(CUSTOMER_ID_PATH)
                .buildAndExpand(customerId)
                .toUri();

        RequestBuilder delBuilder = MockMvcRequestBuilders.delete(deleteUri)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON);

        ResultMatcher noContentMatcher = status().isNoContent();

        // When
        mockMvc.perform(delBuilder).andExpect(noContentMatcher);

        // Then
        then(customerService).should().deleteCustomerById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(customerId);
    }

    @Test
    void able_To_Patch_Customer_By_Id() throws Exception {

        // Given
        UUID customerId = UUID.randomUUID();

        URI patchUri = ServletUriComponentsBuilder.fromUriString(CUSTOMER_URI)
                .path(CUSTOMER_ID_PATH)
                .buildAndExpand(customerId)
                .toUri();

        String newName = "The Best Customer";
        Map<String, Object> patch = Map.of("name", newName);
        String payload = objectMapper.writeValueAsString(patch);

        RequestBuilder patchBuilder = MockMvcRequestBuilders.patch(patchUri)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(payload);

        ResultMatcher noContentMatcher = status().isNoContent();

        // When
        mockMvc.perform(patchBuilder).andExpect(noContentMatcher);

        // Then
        then(customerService).should().patchCustomerById(
                idCaptor.capture(), customerCaptor.capture());

        assertThat(idCaptor.getValue()).isEqualTo(customerId);
        assertThat(customerCaptor.getValue().name()).isEqualTo(newName);
    }

}
