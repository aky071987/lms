package com.cs.lms.repository;

import com.cs.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.phoneNumber = ?1")
    User searchUserByPhoneNumber(String phone);
}
