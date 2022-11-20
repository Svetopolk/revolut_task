package svetopolk;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class Service {

    Set<Account> accounts = new HashSet<>();

    public boolean transfer(Account from, Account to, BigDecimal amount) {
        return sync(from, to, () -> {
            if (withdraw(from, amount)) {
                return add(to, amount);
            }else{
                return false;
            }
        });
    }

    public boolean sync(Account from, Account to, Supplier<Boolean> action) {
        var first = from.compareTo(to) >= 0 ? from : to;
        var second = from.compareTo(to) < 0 ? from : to;
        synchronized (first) {
            synchronized (second) {
                return action.get();
            }
        }
    }

    public boolean add(Account account, BigDecimal amount) {
        synchronized (account) {
            account.setAmount(account.getAmount().add(amount));
        }
        return true;
    }

    public boolean withdraw(Account account, BigDecimal amount) {
        if (account.getAmount().compareTo(amount) >= 0) {
            account.setAmount(account.getAmount().subtract(amount));
            return true;
        }
        return false;
    }

    public Account newAccount() {
        return newAccount(BigDecimal.ZERO);
    }

    public synchronized Account newAccount(BigDecimal amount) {
        Account account = new Account(accounts.size(), amount);
        accounts.add(account);
        return account;
    }

    public BigDecimal getTotal() {
        return accounts.stream().parallel().map(Account::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getAccountNumber() {
        return accounts.size();
    }
}
