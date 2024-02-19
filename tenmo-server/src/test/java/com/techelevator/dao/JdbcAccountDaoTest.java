package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;

public class JdbcAccountDaoTest extends BaseDaoTests{
    protected static final BigDecimal BALANCE = new BigDecimal("100.00");
    protected static final User USER_1 = new User(1001, "user1", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user2", "user2", "USER");
    private static final User USER_3 = new User(1003, "user3", "user3", "USER");
    protected static final Account ACCOUNT_1 = new Account(10005L, USER_1.getId(), BALANCE);
    protected static final Account ACCOUNT_2 = new Account(1006L, USER_2.getId(), BALANCE);
    protected static final Account ACCOUNT_3 = new Account(1007L, USER_3.getId(), BALANCE);
    protected static final String SQL = "INSERT INTO account (account_id, user_id, balance) " +
            "VALUES (10005, 1001, 100)";
    private JdbcAccountDao dao;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        dao = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void mapRowToAccountTest(){
        Account map = null;
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(SQL);
        SqlRowSet results = jdbcTemplate.queryForRowSet("SELECT * FROM account WHERE account_id = 10005");
        if(results.next()) {
            map = dao.mapRowtoAccount(results);
        }
        Assert.assertEquals(map, ACCOUNT_1);
    }

    @Test
    public void transferTest(){
        BigDecimal amount = new BigDecimal(10);
        BigDecimal actualBalance = new BigDecimal("90.00");
        ACCOUNT_3.transfer(ACCOUNT_2, amount);
        Assert.assertEquals(ACCOUNT_3.getBalance(), actualBalance);
    }
}
