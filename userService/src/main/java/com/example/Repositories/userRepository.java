package com.example.Repositories;

import com.example.Model.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface userRepository extends JpaRepository<user,Integer> {
    user findByPhoneNo(String phoneNo);
}
