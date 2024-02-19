package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;
    private UserDao userDao;

    //Created a longer SQL statement that better joins the tables
    //account_from = af
    //account_to = aTo
    private final String sqlTransfer = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.amount, " +
            "af.account_id as fromAcct, af.user_id as fromUser, af.balance as fromBal, " +
            "aTo.account_id as toAcct, aTo.user_id as toUser, aTo.balance as toBal " +
            "FROM transfer t " +
            "JOIN transfer_type tt ON t.transfer_type_id = tt.transfer_type_id "+
            "JOIN transfer_status ts ON t.transfer_status_id = ts.transfer_status_id "+
            "JOIN account af on account_from = af.account_id " +
            "JOIN account aTo on account_to = aTo.account_id ";

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, AccountDao accountDao, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }
    //*Hannah
    @Override
    public Transfer addATransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Account fromAccount = accountDao.getAccountById(transfer.getAccountFrom().getId());
        Account toAccount = accountDao.getAccountById(transfer.getAccountTo().getId());
        Long newId = getTransferId();
        Long transferType = getTransferTypeId(transfer.getTransferType());
        Long transferStatus = getTransferStatusId(transfer.getTransferStatus());
        jdbcTemplate.update(sql, newId, transferType, transferStatus, fromAccount.getAccountId(), toAccount.getAccountId(),
                transfer.getAmount());
        return getTransferById(newId);
    }
    //*Hannah
    private long getTransferId() {
        //Since for some reason I wasn't able to return a transferId
        //This method creates one and assigns it to transfer
        SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_transfer_id')");
        if(nextIdResult.next()) {
            return nextIdResult.getLong("nextval");
        } else {
            throw new RuntimeException("Something went wrong getting id for the new transfer");
        }
    }
    //*Juan
    @Override
    public List<Transfer> getTransfersForUser(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = sqlTransfer +
                "where (account_from IN (SELECT account_id FROM account WHERE user_id = ?) " +
                "OR account_to IN (SELECT account_id FROM account WHERE user_id = ?))";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }
    //*Juan
    public Long getTransferTypeId(String transferType) {
        //method to get the id out of the transfer_type_desc
        //since the transfer table doesn't have transfer_type_desc
        String sql = "SELECT transfer_type_id FROM transfer_type WHERE transfer_type_desc = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferType);
        if(results.next()) {
            return results.getLong("transfer_type_id");
        } else {
            throw new RuntimeException("Unable to find transferType " + transferType);
        }
    }
    //*Caleb
    private Long getTransferStatusId(String transferStatus) {
        //method to get id out of the transfer_status_desc
        //since the transfer table doesn't have transfer_type_desc
        String sql = "SELECT transfer_status_id FROM transfer_status WHERE transfer_status_desc = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferStatus);
        if(results.next()) {
            return results.getLong("transfer_status_id");
        } else {
            throw new RuntimeException("Unable to find transferStatus "+transferStatus);
        }
    }

    //*Juan
    @Override
    public void updateStatus(Transfer transfer) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
        Long transferStatusId = getTransferStatusId(transfer.getTransferStatus());
        jdbcTemplate.update(sql, transferStatusId, transfer.getTransferId());
    }
    //*Caleb
    @Override
    public Transfer getTransferById(Long id) {
        Transfer transfer = null;
        String sql = sqlTransfer + "WHERE transfer_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                transfer = mapRowToTransfer(results);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfer;
    }

    public Transfer mapRowToTransfer(SqlRowSet rowSet) {
        //used the created names of fromUser and toUser that was made in the sqlTransfer String on line 18
        //Line 19 and 20 is where you'll see them being made
        return new Transfer(rowSet.getLong("transfer_id"), rowSet.getString("transfer_type_desc"), rowSet.getString("transfer_status_desc"),
                userDao.getUserById(rowSet.getInt("fromUser")), userDao.getUserById(rowSet.getInt("toUser")), rowSet.getBigDecimal("amount"));
    }
}
