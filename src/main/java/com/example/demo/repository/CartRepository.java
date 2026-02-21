package com.example.demo.repository;

import com.example.demo.model.Cart;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>, JpaSpecificationExecutor<Cart> {

    Optional<Cart> findByUser_Id(Long userId);

    @Query("""
        select distinct c
        from Cart c
        left join fetch c.items i
        left join fetch i.car
        left join fetch c.user
        where c.id = :id
    """)
    Optional<Cart> findWithItemsById(@Param("id") Long id);
}