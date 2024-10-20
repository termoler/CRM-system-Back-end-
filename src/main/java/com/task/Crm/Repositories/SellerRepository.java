package com.task.Crm.Repositories;

import com.task.Crm.Models.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer>{
    Optional<Seller> findByName(String name);
}
