package com.example.demo.repository;

import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIntentId(String intentId);
    Optional<Payment> findTopByOrder_IdOrderByIdDesc(Long orderId);

    @Query("""
        select coalesce(sum(p.amount), 0)
        from Payment p
        where p.status = :status
          and p.completedAt is not null
          and p.completedAt between :from and :to
    """)
    BigDecimal sumAmountByStatusBetween(@Param("status") PaymentStatus status,
                                        @Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);

    @Query("""
        select count(p)
        from Payment p
        where p.status = :status
          and p.completedAt is not null
          and p.completedAt between :from and :to
    """)
    long countByStatusBetween(@Param("status") PaymentStatus status,
                              @Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to);

    @Query("""
        select p
        from Payment p
        where p.status = com.example.demo.model.PaymentStatus.SUCCEEDED
        order by p.completedAt desc
    """)
    List<Payment> findLatestSucceeded(Pageable pageable);

    // last N days revenue (SUCCEEDED) — chart üçün
    @Query("""
        select cast(p.completedAt as date) as day, coalesce(sum(p.amount), 0) as total
        from Payment p
        where p.status = com.example.demo.model.PaymentStatus.SUCCEEDED
          and p.completedAt is not null
          and p.completedAt between :from and :to
        group by cast(p.completedAt as date)
        order by cast(p.completedAt as date)
    """)
    List<DaySumView> sumSucceededByDayBetween(@Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);

    interface DaySumView {
        java.sql.Date getDay();
        BigDecimal getTotal();
    }
}