package com.task.Crm.Controllers;

import com.task.Crm.DTO.TransactionDTO;
import com.task.Crm.Models.Transaction;
import com.task.Crm.util.ExceptionUtil;
import com.task.Crm.util.TransactionException.*;
import com.task.Crm.Services.TransactionService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final ModelMapper modelMapper;

    @Autowired
    public TransactionController(TransactionService transactionService, ModelMapper modelMapper) {
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/getTransactions")
    public List<TransactionDTO> getTransactions() {
        return transactionService.findAll()
                .stream().map(this::convertToTransactionDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/getTransaction/{id}")
    public TransactionDTO getTransactionById(@PathVariable Integer id) {
        return convertToTransactionDTO(transactionService.findOne(id));
    }

    @PostMapping("/createTransaction")
    public ResponseEntity<HttpStatus> createTransaction(@RequestBody @Valid Transaction transactionDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new TransactionNotCreatedException(ExceptionUtil.getException(bindingResult).toString());
        }

        transactionService.save(transactionDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/updateTransaction/{id}")
    public ResponseEntity<HttpStatus> updateTransaction(@PathVariable("id") int id,
                                                         @RequestBody @Valid TransactionDTO transactionDTO,
                                                         BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new TransactionNotUpdateException(ExceptionUtil.getException(bindingResult).toString());
        }
        transactionService.update(id, convertToTransaction(transactionDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/getMaxId")
    public ResponseEntity<Integer> getMaxId() {
        return ResponseEntity.ok(transactionService.getMaxId());
    }

    @DeleteMapping("/deleteTransaction/{id}")
    public ResponseEntity<HttpStatus> deleteTransaction(@PathVariable Integer id) {
        transactionService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<TransactionErrorResponse> handleException(TransactionNotCreatedException exception) {
        TransactionErrorResponse response = new TransactionErrorResponse(
                exception.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    private ResponseEntity<TransactionErrorResponse> handleException(TransactionNotUpdateException exception) {
        TransactionErrorResponse response = new TransactionErrorResponse(
                exception.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    private ResponseEntity<TransactionErrorResponse> handleException(TransactionNotDeleteException exception) {
        TransactionErrorResponse response = new TransactionErrorResponse(
                "There is no transaction with this id in the database, that's why this transaction could not be deleted",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    private ResponseEntity<TransactionErrorResponse> handleException(TransactionNotFoundException exception) {
        TransactionErrorResponse response = new TransactionErrorResponse(
                "There is no transaction with this id in the database!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        return modelMapper.map(transaction, TransactionDTO.class);
    }
    private Transaction convertToTransaction(TransactionDTO transactionDTO) {
        return modelMapper.map(transactionDTO, Transaction.class);
    }
}
