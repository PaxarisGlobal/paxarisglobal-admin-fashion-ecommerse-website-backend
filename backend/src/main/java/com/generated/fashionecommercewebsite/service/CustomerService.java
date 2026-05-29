package com.generated.fashionecommercewebsite.service;

import com.generated.fashionecommercewebsite.dto.CustomerDto;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDto> findAll();

    CustomerDto findById(UUID id);

    CustomerDto create(CustomerDto dto);

    CustomerDto update(UUID id, CustomerDto dto);

    void delete(UUID id);
}
