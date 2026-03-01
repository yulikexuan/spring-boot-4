//: spring.boot.sfg7.rest.mvc.web.controller.CustomerControllerTest.java

package spring.boot.sfg7.rest.mvc.web.controller;


import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import spring.boot.sfg7.rest.mvc.domain.customer.model.Customer;
import spring.boot.sfg7.rest.mvc.domain.customer.service.CustomerService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;


@WebMvcTest(CustomerController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Test CustomerController - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CustomerControllerTest {

    static final String CUSTOMER_URI = "/sfg7/api/v1/customer";
    public static final String CUSTOMER_ID_PATH = "/{customerId}";

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

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

}
