package com.revature.revtickets.repository;

import com.revature.revtickets.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserUserId(Long userId);
    List<Cart> findByUserUserIdAndShowShowId(Long userId, Long showId);
    void deleteByExpiresAtBefore(LocalDateTime expiryTime);
}
