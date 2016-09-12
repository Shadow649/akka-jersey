package com.shad649.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
/**
 * Transaction
 * @author Emanuele Lombardi
 *
 */
public class Transaction {
    private final String userId;
    private final String transactionId;
    private final Currency currencyFrom;
    private final Currency currencyTo;
    private final BigDecimal amountSell;
    private final BigDecimal amountBuy;
    private final BigDecimal rate;
    private final Date timePlaced;
    private final String originatingCountry;
    private final Random random = new Random();
    private final RandomCurrency rc = new RandomCurrency();
    
    public Transaction() {
        this.userId = UUID.randomUUID().toString();
        this.transactionId = UUID.randomUUID().toString();
        this.currencyFrom = getRandomCurrency();
        this.currencyTo = getRandomCurrency();
        this.amountSell = new BigDecimal(random.nextInt(2000));
        this.amountBuy =  new BigDecimal(random.nextInt(2000));
        this.rate = new BigDecimal(random.nextFloat());
        this.timePlaced = new Date();
        this.originatingCountry = "FR";
    }
    
    public Transaction(Currency from, Currency to, Date transactionDate) {
        this.userId = UUID.randomUUID().toString();
        this.transactionId = UUID.randomUUID().toString();
        this.amountSell = new BigDecimal(random.nextInt(2000));
        this.amountBuy =  new BigDecimal(random.nextInt(2000));
        this.rate = new BigDecimal(random.nextFloat());
        this.originatingCountry = "FR";
        this.timePlaced = transactionDate;
        this.currencyFrom = from;
        this.currencyTo = to;
    }
    
    private Currency getRandomCurrency() {
        return rc.next(currencyFrom);
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public Currency getCurrencyFrom() {
        return currencyFrom;
    }
    
    public Currency getCurrencyTo() {
        return currencyTo;
    }
    
    public BigDecimal getAmountSell() {
        return amountSell;
    }
    
    public BigDecimal getRate() {
        return rate;
    }
    
    public Date getTimePlaced() {
        return timePlaced;
    }
    
    public BigDecimal getAmountBuy() {
        return amountBuy;
    }
    
    public String getOriginatingCountry() {
        return originatingCountry;
    }
    
    private class RandomCurrency {
        List<Currency> currencies = new ArrayList<Currency>();
        
        public RandomCurrency() {
            refill();
        }
        
        private void refill() {
            currencies.add(Currency.getInstance("EUR"));
            currencies.add(Currency.getInstance("USD"));
        }

        public Currency next(Currency old) {
            if(currencies.isEmpty()) {
                refill();
            }
            Currency c = currencies.get(random.nextInt(currencies.size()));
            if(c.equals(old)) {
                currencies.remove(old);
                c = next(old);
            }
            return c;
        }
        
    }
}
