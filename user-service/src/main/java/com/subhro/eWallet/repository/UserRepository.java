package com.subhro.eWallet.repository;

import com.subhro.eWallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    User findByPhoneNumber(int number);
}
