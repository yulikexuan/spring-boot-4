//: spring.boot.sfg7.rest.mvc.domain.customer.service.CustomerService.java

package spring.boot.sfg7.rest.mvc.domain.customer.service;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.rest.mvc.domain.customer.dto.CustomerDto;
import spring.boot.sfg7.rest.mvc.domain.customer.dto.CustomerMapper;
import spring.boot.sfg7.rest.mvc.domain.customer.model.Customer;
import spring.boot.sfg7.rest.mvc.domain.customer.repository.CustomerRepository;
import spring.boot.sfg7.rest.mvc.domain.service.NotFoundException;


public interface CustomerService {

    CustomerDto saveNewCustomer(CustomerDto customer);

    List<CustomerDto> findAllCustomers();

    CustomerDto getCustomerById(UUID id);

    void updateCustomerById(UUID customerId, CustomerDto customer);

    void deleteCustomerById(UUID customerId);

    void patchCustomerById(UUID customerId, CustomerDto customer);
}


@Service
@NullMarked
@RequiredArgsConstructor
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerDto saveNewCustomer(@NonNull CustomerDto customer) {

        var data = Customer.builder()
                .version(customer.version())
                .name(customer.name())
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .build();

        return customerMapper.toDto(customerRepository.save(data));
    }

    @Override
    public List<CustomerDto> findAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDto)
                .toList();
    }

    @Override
    public CustomerDto getCustomerById(@NonNull UUID id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDto)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public void updateCustomerById(UUID customerId, CustomerDto customer) {

        var existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(customerId));

        var incoming = customerMapper.toEntity(customer);
        var newCustomer = existingCustomer.updateWith(customerId, incoming);

        customerRepository.save(newCustomer);
    }

    @Override
    public void deleteCustomerById(@NonNull UUID customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public void patchCustomerById(UUID customerId, CustomerDto customer) {

        var existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(customerId));

        var incoming = customerMapper.toEntity(customer);
        var newCustomer = existingCustomer.patchWith(customerId, incoming);

        customerRepository.save(newCustomer);
    }

}
