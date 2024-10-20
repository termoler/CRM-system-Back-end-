package com.task.Crm.Services;

import com.task.Crm.Models.Seller;
import com.task.Crm.Models.Transaction;
import com.task.Crm.util.TransactionException.TransactionNotDeleteException;
import com.task.Crm.util.TransactionException.TransactionNotFoundException;
import com.task.Crm.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final SellerService sellerService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, SellerService sellerService) {
        this.transactionRepository = transactionRepository;
        this.sellerService = sellerService;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findOne(int id) {
        Optional<Transaction> foundPerson = transactionRepository.findById(id);
        return foundPerson.orElseThrow(TransactionNotFoundException::new);
    }

    @Transactional
    public void save(Transaction transaction) {
        enrichTransaction(transaction);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void update(int id, Transaction transaction) {
        Optional<Transaction> foundTransaction = transactionRepository.findById(id);
        if (foundTransaction.isEmpty()) {
            throw new TransactionNotFoundException();
        }
        transaction.setId(id);
        enrichTransaction(transaction);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void delete(int id) {
        Optional<Transaction> foundTransaction = transactionRepository.findById(id);
        Transaction transaction = foundTransaction.orElseThrow(TransactionNotDeleteException::new);
        transactionRepository.delete(transaction);
    }

    private void enrichTransaction(Transaction transaction) {
        Seller seller = sellerService.findById(transaction.getSeller().getId());
        transaction.setSeller(seller);
        transaction.setTransactionDate(LocalDateTime.now());
    }

    public Integer getMaxId() {
        Transaction transaction = transactionRepository.findFirstByOrderByIdDesc();
        return transaction.getId();
    }
}
