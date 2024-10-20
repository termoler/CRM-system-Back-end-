package com.task.Crm.Repositories;

import com.task.Crm.Models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Transaction findFirstByOrderByIdDesc();
}
