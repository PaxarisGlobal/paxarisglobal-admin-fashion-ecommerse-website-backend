package com.generated.fashionecommercewebsite.mapper;

import com.generated.fashionecommercewebsite.dto.CustomerDto;
import com.generated.fashionecommercewebsite.entity.Customer;

public class CustomerMapper {
    private CustomerMapper() {}

    public static CustomerDto toDto(Customer entity) {
        CustomerDto dto = new CustomerDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setCompany(entity.getCompany());
        return dto;
    }

    public static Customer toEntity(CustomerDto dto) {
        Customer entity = new Customer();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setCompany(dto.getCompany());
        return entity;
    }
}
