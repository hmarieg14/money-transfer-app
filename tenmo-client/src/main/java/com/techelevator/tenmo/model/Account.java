package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private Long accountId;
    private int userID;
    private BigDecimal balance;
    private User user;

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

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

    public String getUsername() {
        return user.getUsername();
    }
}
