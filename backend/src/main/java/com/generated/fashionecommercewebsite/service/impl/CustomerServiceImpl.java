package com.generated.fashionecommercewebsite.service.impl;

import com.generated.fashionecommercewebsite.dto.CustomerDto;
import com.generated.fashionecommercewebsite.entity.Customer;
import com.generated.fashionecommercewebsite.exception.ResourceNotFoundException;
import com.generated.fashionecommercewebsite.mapper.CustomerMapper;
import com.generated.fashionecommercewebsite.repository.CustomerRepository;
import com.generated.fashionecommercewebsite.service.CustomerService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream().map(CustomerMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CustomerDto findById(UUID id) {
        Customer customer = customerRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        return CustomerMapper.toDto(customer);
    }

    @Override
    public CustomerDto create(CustomerDto dto) {
        Customer customer = CustomerMapper.toEntity(dto);
        customer.setId(null);
        return CustomerMapper.toDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDto update(UUID id, CustomerDto dto) {
        Customer existing = customerRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setCompany(dto.getCompany());
        return CustomerMapper.toDto(customerRepository.save(existing));
    }

    @Override
    public void delete(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found: " + id);
        }
        customerRepository.deleteById(id);
    }
}
