package com.generated.fashionecommercewebsite.repository;

import com.generated.fashionecommercewebsite.entity.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
