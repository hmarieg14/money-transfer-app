package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    Transfer addATransfer(Transfer transfer);

    List<Transfer> getTransfersForUser(int userId);

    void updateStatus(Transfer transfer);

    Transfer getTransferById(Long id);

}
