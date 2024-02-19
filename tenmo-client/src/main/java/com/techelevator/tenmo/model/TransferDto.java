package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferDto {
    private long accountFrom;
    private long accountTo;
    private BigDecimal amount;
    private String transferType;

    public TransferDto(long fromUserId, long toUserId, BigDecimal amount, String transferType) {
        validateTransferType(transferType);
        this.accountFrom = fromUserId;
        this.accountTo = toUserId;
        this.amount = amount;
        this.transferType = transferType;
    }

    public long getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(long accountFrom) {
        this.accountFrom = accountFrom;
    }

    public long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(long accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        validateTransferType(transferType);
        this.transferType = transferType;
    }

    private void validateTransferType(String transferType) {
        if(!TransferType.isValid(transferType)) {
            throw new IllegalArgumentException(transferType+" is not a valid transferType");
        }
    }


}
