package com.task.Crm.Controllers;

import com.task.Crm.Models.Seller;
import com.task.Crm.Models.Transaction;
import com.task.Crm.Services.SellerService;
import com.task.Crm.util.ExceptionUtil;
import com.task.Crm.util.SellerExceptions.SellerErrorResponse;
import com.task.Crm.util.SellerExceptions.SellerNotFoundException;
import com.task.Crm.DTO.SellerDTO;
import com.task.Crm.DTO.TransactionDTO;
import com.task.Crm.util.EmptyResponseToRequest;
import com.task.Crm.util.IncorrectPeriod;
import com.task.Crm.util.SellerExceptions.SellerNotCreatedException;
import com.task.Crm.util.SellerExceptions.SellerNotUpdatedException;
import com.task.Crm.util.TransactionException.TransactionEmptyException;
import com.task.Crm.util.TransactionException.TransactionErrorResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final ModelMapper modelMapper;

    @Autowired
    public SellerController(SellerService sellerService, ModelMapper modelMapper) {
        this.sellerService = sellerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/getSellers")
    public List<SellerDTO> getSellers() {
        return sellerService.findAll()
                .stream().map(this::convertToSellerDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/getSellerById/{id}")
    public SellerDTO getSellerById(@PathVariable("id") int id) {
        return convertToSellerDTO(sellerService.findById(id));
    }

    @GetMapping("/getSellerByName/{name}")
    public SellerDTO getSellerByName(@PathVariable("name") String name) {
        return convertToSellerDTO(sellerService.findByName(name));
    }

    @GetMapping("/getTransactionBySellerId/{id}")
    public List<TransactionDTO> getTransactionsBySellerId(@PathVariable("id") int id) {
        return sellerService.getTransactionsBySellerId(id)
                .stream().map(this::convertToTransactionDTO)
                .collect(Collectors.toList());
    }
    @GetMapping("/getBestSellerForPeriod/{period}")
    public List<SellerDTO> getBestSellerForPeriod(@PathVariable("period") String period,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return sellerService.getBestSellerForPeriod(period, startDate, endDate)
                .stream().map(this::convertToSellerDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/getSellersBelowAmountForPeriod/{amount}")
    public List<SellerDTO> getSellersBelowAmountForPeriod(@PathVariable double amount,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return sellerService.getSellersBelowAmountForPeriod(amount, startDate, endDate)
                .stream().map(this::convertToSellerDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/createSeller")
    public ResponseEntity<HttpStatus> createSeller(@RequestBody @Valid SellerDTO sellerDTO,
                                                   BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new SellerNotCreatedException(ExceptionUtil.getException(bindingResult).toString());
        }
        sellerService.save(convertToSeller(sellerDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @PatchMapping("/updateSeller/{id}")
    public ResponseEntity<HttpStatus> updateSeller(@PathVariable("id") int id,
                                                   @RequestBody @Valid SellerDTO sellerDTO,
                                                   BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new SellerNotUpdatedException(ExceptionUtil.getException(bindingResult).toString());
        }
        sellerService.update(id, convertToSeller(sellerDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @DeleteMapping("/deleteSeller/{id}")
    public ResponseEntity<HttpStatus> deleteSeller(@PathVariable("id") int id){
        sellerService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<SellerErrorResponse> handleException(SellerNotFoundException exception) {
        SellerErrorResponse response = new SellerErrorResponse(
                "Seller with this id wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<SellerErrorResponse> handleException(SellerNotUpdatedException exception) {
        SellerErrorResponse response = new SellerErrorResponse(
                exception.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<SellerErrorResponse> handleException(SellerNotCreatedException exception) {
        SellerErrorResponse response = new SellerErrorResponse(
                exception.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<TransactionErrorResponse> handleException(TransactionEmptyException exception) {
        TransactionErrorResponse response = new TransactionErrorResponse(
                "This seller has not done any transactions yet",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<SellerErrorResponse> handleException(IncorrectPeriod exception) {
        SellerErrorResponse response = new SellerErrorResponse(
                exception.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    private ResponseEntity<SellerErrorResponse> handleException(EmptyResponseToRequest exception) {
        SellerErrorResponse response = new SellerErrorResponse(
                exception.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Seller convertToSeller(SellerDTO sellerDTO) {
        return modelMapper.map(sellerDTO, Seller.class);
    }

    private SellerDTO convertToSellerDTO(Seller seller) {
        return modelMapper.map(seller, SellerDTO.class);
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        return modelMapper.map(transaction, TransactionDTO.class);
    }
}
