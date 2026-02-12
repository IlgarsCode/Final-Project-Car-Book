package com.example.demo.repository;

import com.example.demo.dto.enums.PricingRateType;
import com.example.demo.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCart_IdAndCar_IdAndRateType(Long cartId, Long carId, PricingRateType rateType);

    Optional<CartItem> findByIdAndCart_Id(Long id, Long cartId);

    long countByCart_Id(Long cartId);

    void deleteByCart_Id(Long cartId);

    // ✅ projection (nested interface)
    interface CartCountView {
        Long getCartId();
        Long getCnt();
    }

    // ✅ bulk count: N+1 olmasın
    @Query("""
        select ci.cart.id as cartId, coalesce(sum(ci.quantity),0) as cnt
        from CartItem ci
        where ci.cart.id in :cartIds
        group by ci.cart.id
    """)
    List<CartCountView> countItemsByCartIds(@Param("cartIds") List<Long> cartIds);
}
