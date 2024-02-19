package com.techelevator.tenmo.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

public class TransferDtoServer {

    private int accountFrom;
    private int accountTo;
    @DecimalMin(value = "0.0")
    private BigDecimal amount;
    @NotEmpty
    private String transferType;

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
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
        this.transferType = transferType;
    }


    @AssertTrue
    public boolean isValidTransferType() {
        return Transfer.TRANSFER_TYPE_REQUEST.equals(this.transferType) || Transfer.TRANSFER_TYPE_SEND.equals(this.transferType);
    }
}
