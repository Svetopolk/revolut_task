package svetopolk;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

class ServiceTest {

    @Test
    void createAccount() {
        var service = new Service();
        var account = service.newAccount(new BigDecimal("123.12"));

        assertThat(account.getAmount(), is(new BigDecimal("123.12")));
    }

    @Test
    void getTotal() {
        var service = new Service();
        var account = service.newAccount();
        service.add(account, new BigDecimal("123.12"));
        assertThat(service.getTotal(), is(new BigDecimal("123.12")));
    }

    @Test
    void accountCompareTo() {
        Service service = new Service();
        var account1 = service.newAccount();
        var account2 = service.newAccount();
        assertThat(account2, greaterThan(account1));
        assertThat(account1, lessThan(account2));
    }

    @Test
    void addToAccount() {
        var service = new Service();
        var account = service.newAccount(new BigDecimal("1.11"));
        assertThat(service.add(account, new BigDecimal("1.12")), is(true));
        assertThat(account.getAmount(), is(new BigDecimal("2.23")));
    }

    @Test
    void withdrawFromAccount() {
        var service = new Service();
        var account = service.newAccount(new BigDecimal("7.00"));
        assertThat(service.withdraw(account, new BigDecimal("5.00")), is(true));
        assertThat(service.withdraw(account, new BigDecimal("3.00")), is(false));
        assertThat(account.getAmount(), is(new BigDecimal("2.00")));
        assertThat(service.withdraw(account, new BigDecimal("2.00")), is(true));
        assertThat(account.getAmount(), is(new BigDecimal("0.00")));
    }

    @Test
    void transferFromAccountToAccount() {
        var service = new Service();
        var account1 = service.newAccount(new BigDecimal("1000"));
        var account2 = service.newAccount();

        assertThat(service.transfer(account1, account2, BigDecimal.ONE), is(true));
        assertThat(account1.getAmount(), is(new BigDecimal("999")));
        assertThat(account2.getAmount(), is(new BigDecimal("1")));

        assertThat(service.transfer(account1, account2, new BigDecimal("1000")), is(false));
        assertThat(account1.getAmount(), is(new BigDecimal("999")));
        assertThat(account2.getAmount(), is(new BigDecimal("1")));
    }

    @Test
    void transferToItself() {
        var service = new Service();
        var account = service.newAccount(BigDecimal.TEN);

        assertThat(service.transfer(account, account, BigDecimal.ONE), is(true));
        assertThat(account.getAmount(), is(BigDecimal.TEN));
    }

    @Test
    void createManyAccounts() {
        var service = new Service();
        IntStream.range(0, 100000).parallel().forEach(x -> service.newAccount(BigDecimal.valueOf(x)));

        assertThat(service.getAccountNumber(), is(100000));
        assertThat(service.getTotal(), is(new BigDecimal("4999950000")));
    }

    @Test
    void addManyToAccount() {
        var service = new Service();
        var account = service.newAccount();
        IntStream.range(0, 100000).parallel().forEach(x -> service.add(account, BigDecimal.ONE));
        assertThat(account.getAmount(), is(new BigDecimal("100000")));
    }

    @Test
    void withdrawManyFromAccount() {
        var service = new Service();
        var account = service.newAccount();
        IntStream.range(0, 100000).forEach(x -> service.add(account, BigDecimal.ONE));
        IntStream.range(0, 100000).forEach(x -> service.withdraw(account, BigDecimal.ONE));
        assertThat(account.getAmount(), is(BigDecimal.ZERO));
    }

    @Test
    void transferManyFromOne() {
        var service = new Service();
        var account1 = service.newAccount(new BigDecimal("100000"));
        var account2 = service.newAccount();

        IntStream.range(0, 100000).parallel().forEach(x -> service.transfer(account1, account2, BigDecimal.ONE));
        assertThat(account1.getAmount(), is(BigDecimal.ZERO));
        assertThat(account2.getAmount(), is(new BigDecimal("100000")));
    }

    @Test
    void transferRandomly() {
        var service = new Service();
        var account1 = service.newAccount(new BigDecimal("50000"));
        var account2 = service.newAccount(new BigDecimal("50000"));
        assertThat(service.getTotal(), is(new BigDecimal("100000")));

        var random = new Random();
        IntStream.range(0, 100000).parallel().forEach(x -> {
            if (random.nextBoolean()) {
                service.transfer(account1, account2, BigDecimal.ONE);
            } else {
                service.transfer(account2, account1, BigDecimal.ONE);
            }
        });
        assertThat(service.getTotal(), is(new BigDecimal("100000")));
    }


}