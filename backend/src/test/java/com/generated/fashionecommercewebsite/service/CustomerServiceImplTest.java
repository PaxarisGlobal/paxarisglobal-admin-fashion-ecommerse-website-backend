package com.generated.fashionecommercewebsite.service;

import com.generated.fashionecommercewebsite.dto.CustomerDto;
import com.generated.fashionecommercewebsite.entity.Customer;
import com.generated.fashionecommercewebsite.exception.ResourceNotFoundException;
import com.generated.fashionecommercewebsite.mapper.CustomerMapper;
import com.generated.fashionecommercewebsite.repository.CustomerRepository;
import com.generated.fashionecommercewebsite.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private UUID customerId;
    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        
        customer = new Customer();
        customer.setId(customerId);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setCompany("ACME Corp");

        customerDto = new CustomerDto();
        customerDto.setId(customerId);
        customerDto.setName("John Doe");
        customerDto.setEmail("john@example.com");
        customerDto.setCompany("ACME Corp");
    }

    @Test
    void testFindAll() {
        List<Customer> customers = List.of(customer);
        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerDto> result = customerService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerDto result = customerService.findById(customerId);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void testFindByIdNotFound() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.findById(customerId));
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void testCreate() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto result = customerService.create(customerDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdate() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto updated = new CustomerDto();
        updated.setId(customerId);
        updated.setName("Jane Doe");
        updated.setEmail("jane@example.com");
        updated.setCompany("ACME Inc");

        CustomerDto result = customerService.update(customerId, updated);

        assertNotNull(result);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testDelete() {
        when(customerRepository.existsById(customerId)).thenReturn(true);

        customerService.delete(customerId);

        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void testDeleteNotFound() {
        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> customerService.delete(customerId));
    }
}
