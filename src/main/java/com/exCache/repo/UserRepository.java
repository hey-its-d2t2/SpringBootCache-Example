package com.exCache.repo;

import com.exCache.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<Object> findByAge(int age);
}