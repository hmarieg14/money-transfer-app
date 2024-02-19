package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.techelevator.tenmo.model.Transfer;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {
    private final TransferDao transferDao;
    private final AccountDao accountDao;
    private final UserDao userDao;

    public TransferController(TransferDao transferDao, AccountDao accountDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable Long id, Principal principal) {
        Transfer transfer = transferDao.getTransferById(id);
        userPrincipleView(transfer, principal);
        return transfer;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "", method = RequestMethod.POST)
    public Transfer createTransfer(@Valid @RequestBody TransferDtoServer transferDto, Principal principal) {
        Transfer newTransfer = buildTransfer(transferDto);
        authorizedToCreate(newTransfer, principal);
        if (newTransfer.isApproved()) {
            transferMoney(newTransfer);
        }
        newTransfer = transferDao.addATransfer(newTransfer);
        return newTransfer;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public Transfer updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdate dto, Principal principal){
        String status = dto.getTransferStatus();
        Transfer transfer = transferDao.getTransferById(id);
        authorizedToUpdate(transfer, principal);
        if(Transfer.TRANSFER_STATUS_APPROVED.equals(status)){
            transfer.approved();
            transferMoney(transfer);
        } else if(Transfer.TRANSFER_STATUS_REJECTED.equals(status)){
            transfer.rejected();
        }
        transferDao.updateStatus(transfer);
        return transfer;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    //This method links up with the getAllTransfers method in transferService
    public List<Transfer> getTransfers(Principal principal) {
        return transferDao.getTransfersForUser(userPrinciple(principal));
    }


    private void userPrincipleView(Transfer transfer, Principal principal){
        TransferPrincipal user = new TransferPrincipal(transfer, principal);
        if (!user.isAllowedToView()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private void authorizedToCreate(Transfer transfer, Principal principal) {
        TransferPrincipal user = new TransferPrincipal(transfer, principal);
        if (!user.isAllowedToCreate()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private void authorizedToUpdate(Transfer transfer, Principal principal){
        TransferPrincipal user = new TransferPrincipal(transfer, principal);
        if(!user.isAllowedToApproveOrReject()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private void transferMoney(Transfer transfer) {
        Account accountFrom = accountDao.getAccountById(transfer.getAccountFrom().getId());
        Account accountTo = accountDao.getAccountById(transfer.getAccountTo().getId());
        accountFrom.transfer(accountTo, transfer.getAmount());
        accountDao.updateBalance(accountFrom);
        accountDao.updateBalance(accountTo);
    }

    private Transfer buildTransfer(TransferDtoServer transfer){
        User fromUser = userDao.getUserById(transfer.getAccountFrom());
        User toUser = userDao.getUserById(transfer.getAccountTo());
        return new Transfer(transfer.getTransferType(), fromUser, toUser, transfer.getAmount());
    }

    private int userPrinciple(Principal principal){
        return userDao.getUserByUsername(principal.getName()).getId();
    }
}
