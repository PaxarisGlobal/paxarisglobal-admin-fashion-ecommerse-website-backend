package com.generated.fashionecommercewebsite.repository;

import com.generated.fashionecommercewebsite.entity.Customer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
