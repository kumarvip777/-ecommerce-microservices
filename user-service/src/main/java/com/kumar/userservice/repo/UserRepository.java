package com.kumar.userservice.repo;

import com.kumar.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String phoneNumber);

    List<User> findByUserNameContainingIgnoreCase(String firstName);

    Optional<User> findByEmail(String email);
}
