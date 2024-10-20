package com.task.Crm.Services;

import com.task.Crm.Repositories.SellerRepository;
import com.task.Crm.util.SellerExceptions.SellerNotFoundException;
import com.task.Crm.Models.Seller;
import com.task.Crm.Models.Transaction;
import com.task.Crm.DAO.SellerDAO;
import com.task.Crm.util.EmptyResponseToRequest;
import com.task.Crm.util.IncorrectPeriod;
import com.task.Crm.util.TransactionException.TransactionEmptyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SellerService {
    private final SellerDAO sellerDao;
    private final SellerRepository sellerRepository;

    @Autowired
    public SellerService(SellerDAO sellerDao, SellerRepository sellerRepository) {
        this.sellerDao = sellerDao;
        this.sellerRepository = sellerRepository;
    }

    public List<Seller> findAll() {
        return sellerDao.getSellers();
    }

    public Seller findById(int id) {
        Optional<Seller> foundPerson = sellerRepository.findById(id);
        return foundPerson.orElseThrow(SellerNotFoundException::new);
    }

    public Seller findByName(String name) {
        Optional<Seller> foundPerson = sellerRepository.findByName(name);
        return foundPerson.orElseThrow(SellerNotFoundException::new);
    }

    public List<Transaction> getTransactionsBySellerId(int id) {
        Optional<Seller> foundSeller = sellerRepository.findById(id);
        Seller seller = foundSeller.orElseThrow(SellerNotFoundException::new);
        List<Transaction> transactionList = seller.getTransactionList();
        if (transactionList.isEmpty()) {
            throw new TransactionEmptyException();
        }
        return seller.getTransactionList();
    }

    private String getMood(String period, LocalDateTime startDate, LocalDateTime endDate) {
        Period foundPeriod = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
        Map<String, Period> periodMap = Map.of(
                "year", Period.of(1, 0, 0),
                "month", Period.of(0, 1, 0),
                "day", Period.of(0, 0, 1),
                "quarter", Period.of(0, 3, 0)
        );
        if (periodMap.containsKey(period)) {
            Period expectedPeriod = periodMap.get(period);
            if (foundPeriod.equals(expectedPeriod)) {
                return period;
            }
        } else if ("specifiedDates".equals(period)) {
            return period;
        }
        return null;
    }

    public List<Seller> getBestSellerForPeriod(String period, LocalDateTime startDate, LocalDateTime endDate){
        if(startDate == null){
            throw new IncorrectPeriod("Start date cannot be null");
        }
        if(endDate == null){
            switch (period) {
                case "year" -> endDate = startDate.plusYears(1);
                case "month" -> endDate = startDate.plusMonths(1);
                case "day" -> endDate = startDate.plusDays(1);
                case "quarter" -> throw new IncorrectPeriod("If period is quarter, then end date cannot be null");
                default -> endDate = startDate.plusDays(1);
            }
        }

        String mood = getMood(period, startDate, endDate);

        if(mood == null){
            throw new IncorrectPeriod("Incorrect period: " + period + "."
                    + " Start date: " + startDate + "."
                    + " End date: " + endDate);
        }

        List<Seller> sellers = sellerDao.getBestSellerForPeriod(mood, startDate, endDate);
        if(sellers.isEmpty()){
            throw new EmptyResponseToRequest(
                    "No sellers were found for this get request: "
                            + "getBestSellerForPeriod"
            );
        }
        return sellerDao.getBestSellerForPeriod(mood, startDate, endDate);
    }

    public List<Seller> getSellersBelowAmountForPeriod(double amount, LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate == null || endDate == null){
            throw new IncorrectPeriod("Start date cannot be null and end date cannot be null");
        }
        List<Seller> sellers = sellerDao.getSellersBelowAmountForPeriod(amount, startDate, endDate);
        if(sellers.isEmpty()){
            throw new EmptyResponseToRequest(
                    "No sellers were found for this get request: "
                    + "getSellersBelowAmountForPeriod"
            );
        }
        return sellers;
    }

    @Transactional
    public void save(Seller seller) {
        enrichSeller(seller);
        sellerRepository.save(seller);
    }

    @Transactional
    public void update(int id, Seller seller) {
        Seller foundSeller = findById(id);
        seller.setId(id);
        if(seller.getContact_info() == null){
            seller.setContact_info(foundSeller.getContact_info());
        }
        seller.setRegistrationDate(foundSeller.getRegistrationDate());
        sellerRepository.save(seller);
    }

    @Transactional
    public void delete(int id) {
        Optional<Seller> foundSeller = sellerRepository.findById(id);
        Seller seller = foundSeller.orElseThrow(SellerNotFoundException::new);
        sellerRepository.delete(seller);
    }
    private void enrichSeller(Seller seller) {
        seller.setRegistrationDate(LocalDateTime.now());
    }



}