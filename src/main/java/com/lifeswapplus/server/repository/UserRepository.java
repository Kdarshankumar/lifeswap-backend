package com.lifeswapplus.server.repository;

import com.lifeswapplus.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query methods
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
