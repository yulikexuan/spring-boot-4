//: spring.boot.sfg7.rest.mvc.web.controller.CustomerController.java

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
import org.springframework.web.bind.annotation.RestController;
import spring.boot.sfg7.rest.mvc.domain.customer.dto.CustomerDto;
import spring.boot.sfg7.rest.mvc.domain.customer.service.CustomerService;
import spring.boot.sfg7.rest.mvc.web.util.WebUtils;


@Slf4j
@RestController
@RequestMapping(CustomerController.CUSTOMER_PATH)
@RequiredArgsConstructor
class CustomerController {

    public static final String CUSTOMER_PATH = "/sfg7/api/v1/customer";

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerDto> listCustomers() {

        return customerService.findAllCustomers();
    }

    @GetMapping(value = "{customerId}")
    public CustomerDto getCustomerById(@PathVariable UUID customerId) {

        log.debug(">>> Get Customer by Id - in controller");

        return customerService.getCustomerById(customerId);
    }

    @PostMapping
    public ResponseEntity<URI> saveNewCustomer(@RequestBody CustomerDto customer) {

        var newCustomer = customerService.saveNewCustomer(customer);

        URI location = WebUtils.buildLocation("/{customerId}", newCustomer::id);

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{customerId}")
    public ResponseEntity<Void> updateCustomerById(
            @PathVariable UUID customerId, @RequestBody CustomerDto customer) {

        customerService.updateCustomerById(customerId, customer);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable UUID customerId) {

        customerService.deleteCustomerById(customerId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{customerId}")
    public ResponseEntity<Void> patchCustomerById(
            @PathVariable UUID customerId, @RequestBody CustomerDto customer) {

        customerService.patchCustomerById(customerId, customer);

        return ResponseEntity.noContent().build();
    }
} /// :~