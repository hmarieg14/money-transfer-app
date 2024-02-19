package com.techelevator.dao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class JdbcTransferDaoTest extends BaseDaoTests {
    public static BigDecimal amount = new BigDecimal(100);
    protected static final User USER_1 = new User(1001, "user1", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user2", "user2", "USER");
    private static final Transfer TRANSFER = new Transfer("Send", USER_1, USER_2, amount);

    private JdbcTransferDao dao;
    private JdbcAccountDao accountDao;
    private UserDao userDao;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        dao = new JdbcTransferDao(jdbcTemplate, accountDao, userDao);
    }

    @Test
    public void getTransfersForUserTest() {
        List<Transfer> actualTransfer = new ArrayList<>();
        actualTransfer.add(TRANSFER);
        dao.getTransfersForUser(TRANSFER.getAccountFrom().getId());
        Assert.assertEquals(1, actualTransfer.size());
        Assert.assertEquals(TRANSFER, actualTransfer.get(0));
    }

    @Test
    public void getTransferTypeId(){
        Long transferId = dao.getTransferTypeId(TRANSFER.getTransferType());
        TRANSFER.setTransferType(String.valueOf(2));
        Assert.assertEquals(Long.valueOf(TRANSFER.getTransferType()), transferId);
    }

    @Test
    public void updateStatusTest(){
        dao.updateStatus(TRANSFER);
        Assert.assertEquals(TRANSFER.getTransferStatus(), "Approved");
    }


    @Test
    public void rejectedWhenPendingShouldSetStatusToRejected() {

        Transfer transfer = new Transfer("Request", new User(), new User(), BigDecimal.TEN);
        transfer.setTransferStatus(Transfer.TRANSFER_STATUS_PENDING);


        transfer.rejected();


        assertEquals(Transfer.TRANSFER_STATUS_REJECTED, transfer.getTransferStatus());
    }

    @Test
    public void rejectedWhenNotPendingShouldThrowBadRequestException() {

        Transfer transfer = new Transfer("Request", new User(), new User(), BigDecimal.TEN);
        transfer.setTransferStatus(Transfer.TRANSFER_STATUS_APPROVED);


        ResponseStatusException exception = assertThrows(ResponseStatusException.class, transfer::rejected);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void rejectedWhenPendingShouldNotThrowException() {

        Transfer transfer = new Transfer("Request", new User(), new User(), BigDecimal.TEN);
        transfer.setTransferStatus(Transfer.TRANSFER_STATUS_PENDING);


        assertDoesNotThrow(transfer::rejected);
    }


}
