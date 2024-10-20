package com.task.Crm.DAO;

import com.task.Crm.Models.Seller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class SellerDAO {
    private final EntityManager em;

    @Autowired
    public SellerDAO(EntityManager em) {
        this.em = em;
    }

    public List<Seller> getSellers(){
        Session session = em.unwrap(Session.class);
        Set<Seller> sellers = new HashSet<Seller>(session.createQuery("select s from Seller s " +
                        "left join fetch s.transactionList")
                        .getResultList());

        return sellers.stream().toList();
    }

    public List<Seller> getBestSellerForPeriod(String period, LocalDateTime startDate, LocalDateTime endDate){
        Query query = getQuery(period, startDate, endDate);

        List<Object[]> sellers = query.getResultList();

        return getListWithBestSellers(sellers);
    }

    private Query getQuery(String period, LocalDateTime startDate, LocalDateTime endDate) {
        Session session = em.unwrap(Session.class);
        Query query = null;
        switch (period) {
            case "year" -> {
                query = session.createQuery("select t.seller, sum(t.amount) as sumAmount from Transaction t " +
                                "where year(t.transactionDate) = :year " +
                                "group by t.seller " +
                                "order by sumAmount desc")
                        .setParameter("year", startDate.getYear());
            }
            case "month" -> {
                query = session.createQuery("select t.seller, sum(t.amount) as sumAmount from Transaction t " +
                                "where month(t.transactionDate) = :month " +
                                "and year(t.transactionDate) = :year " +
                                "group by t.seller " +
                                "order by sumAmount desc")
                        .setParameter("month", startDate.getMonthValue())
                        .setParameter("year", startDate.getYear());
            }
            case "day" -> {
                query = session.createQuery("select t.seller, sum(t.amount) as sumAmount from Transaction t " +
                                "where date(t.transactionDate) = :date " +
                                "group by t.seller " +
                                "order by sumAmount desc")
                        .setParameter("date", startDate);
            }

            case "quarter", "specifiedDates" -> {
                query = session.createQuery("select t.seller, sum(t.amount) as sumAmount from Transaction t " +
                                "where t.transactionDate >= :startDate and t.transactionDate <= :endDate " +
                                "group by t.seller " +
                                "order by sumAmount desc")
                        .setParameter("startDate", startDate)
                        .setParameter("endDate", endDate);
            }
        }
        return query;
    }

    private List<Seller> getListWithBestSellers(List<Object[]> sellers) {
        List<Seller> result = new ArrayList<>();
        if (sellers.isEmpty()) {
            return result;
        }
        Long maxAmount = (Long) sellers.getFirst()[1];
        for(Object[] row : sellers){
            Long sumAmount = (Long) row[1];
            Seller seller = (Seller) row[0];
            if(Objects.equals(sumAmount, maxAmount)){
                result.add(seller);
            }
        }
        return result;
    }

    public List<Seller> getSellersBelowAmountForPeriod(double amount, LocalDateTime startDate, LocalDateTime endDate){
        Session session = em.unwrap(Session.class);
        Set<Seller> sellers = new HashSet<>(
                session.createQuery("select s from Seller s " +
                                "join s.transactionList t " +
                                "where t.transactionDate between :startDate and :endDate " +
                                "group by s.id " +
                                "having sum (t.amount) < :amount", Seller.class)
                        .setParameter("startDate", startDate)
                        .setParameter("endDate", endDate)
                        .setParameter("amount", amount)
                        .getResultList());

        return sellers.stream().toList();
    }
}
