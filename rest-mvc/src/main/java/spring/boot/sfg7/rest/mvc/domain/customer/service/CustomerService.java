//: spring.boot.sfg7.rest.mvc.domain.customer.service.CustomerService.java

package spring.boot.sfg7.rest.mvc.domain.customer.service;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.rest.mvc.domain.customer.model.Customer;
import spring.boot.sfg7.rest.mvc.domain.customer.repository.CustomerRepository;


public interface CustomerService {

    Customer saveNewCustomer(Customer customer);

    List<Customer> findAllCustomers();

    Customer getCustomerById(UUID id);

    void updateCustomerById(UUID customerId, Customer customer);

    void deleteCustomerById(UUID customerId);

    void patchCustomerById(UUID customerId, Customer customer);
}


@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer saveNewCustomer(@NonNull Customer customer) {

        var data = Customer.builder()
                .version(customer.version())
                .name(customer.name())
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        return customerRepository.save(data);
    }

    @Override
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public @Nullable Customer getCustomerById(UUID id) {
        // Do we need to throw an exception here when nothing found?
        return customerRepository.findById(id).orElseThrow();
    }

    @Override
    public void updateCustomerById(UUID customerId, Customer customer) {

        var existingCustomer = getCustomerById(customerId);

        var newCustomer = existingCustomer.updateWith(
                customerId, existingCustomer);

        customerRepository.save(newCustomer);
    }

    @Override
    public void deleteCustomerById(@NonNull UUID customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public void patchCustomerById(UUID customerId, Customer customer) {

        var existingCustomer = getCustomerById(customerId);

        var newCustomer = existingCustomer.patchWith(
                customerId, customer);

        customerRepository.save(newCustomer);
    }

}