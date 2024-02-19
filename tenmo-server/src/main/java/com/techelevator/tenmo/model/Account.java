package com.techelevator.tenmo.model;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Objects;
@Component
public class Account {
    private Long accountId;
    private int userID;
    private BigDecimal balance;

    public Account(Long accountId, int userID, BigDecimal balance) {
        this.accountId = accountId;
        this.userID = userID;
        this.balance = balance;
    }

    public Account() {
    }

    public int getUserID() {
        return userID;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return userID == account.userID && Objects.equals(accountId, account.accountId) && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userID, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userID=" + userID +
                ", balance=" + balance +
                '}';
    }
    //*Juan
    public void transfer(Account accountTo, BigDecimal transferAmount) {
        if(this.balance.compareTo(transferAmount) >= 0) {
            this.balance = this.balance.subtract(transferAmount);
            accountTo.balance = accountTo.balance.add(transferAmount);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, transferAmount + " exceeds the remaining balance of " + this.balance);
        }
    }
}
