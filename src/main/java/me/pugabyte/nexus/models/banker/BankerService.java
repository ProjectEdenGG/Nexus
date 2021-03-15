package me.pugabyte.nexus.models.banker;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Banker.class)
public class BankerService extends MongoService {
	private final static Map<UUID, Banker> cache = new HashMap<>();

	public Map<UUID, Banker> getCache() {
		return cache;
	}

	public double getBalance(OfflinePlayer player) {
		return this.<Banker>get(player).getBalance().doubleValue();
	}

	public String getBalanceFormatted(OfflinePlayer player) {
		return this.<Banker>get(player).getBalanceFormatted();
	}

	public boolean has(OfflinePlayer player, double money) {
		return this.<Banker>get(player).has(money);
	}

	public boolean has(OfflinePlayer player, BigDecimal money) {
		return this.<Banker>get(player).has(money);
	}

	public void deposit(OfflinePlayer player, double amount, TransactionCause cause) {
		deposit(player, BigDecimal.valueOf(amount), cause);
	}

	public void deposit(OfflinePlayer player, BigDecimal money, TransactionCause cause) {
		Banker banker = get(player);
		banker.deposit(money, cause);
		save(banker);
	}

	public void withdraw(OfflinePlayer player, double amount, TransactionCause cause) {
		withdraw(player, BigDecimal.valueOf(amount), cause);
	}

	public void withdraw(OfflinePlayer player, BigDecimal money, TransactionCause cause) {
		Banker banker = get(player);
		banker.withdraw(money, cause);
		save(banker);
	}

	public void transfer(OfflinePlayer from, OfflinePlayer to, double amount, TransactionCause cause) {
		transfer(from, to, BigDecimal.valueOf(amount), cause);
	}

	public void transfer(OfflinePlayer from, OfflinePlayer to, BigDecimal money, TransactionCause cause) {
		transfer((Banker) get(from), get(to), money, cause);
	}

	public void transfer(Banker from, Banker to, BigDecimal money, TransactionCause cause) {
		from.transfer(to, money, cause);
		save(from);
		save(to);
	}

	public void transfer(OfflinePlayer from, OfflinePlayer to, BigDecimal money, Transaction transaction) {
		transfer(get(from), this.<Banker>get(to), money, transaction);
	}

	public void transfer(Banker from, Banker to, BigDecimal money, Transaction transaction) {
		from.transfer(to, money, transaction);
		save(from);
		save(to);
	}

	public void setBalance(OfflinePlayer player, double balance, TransactionCause cause) {
		setBalance(player, BigDecimal.valueOf(balance), cause);
	}

	public void setBalance(OfflinePlayer player, BigDecimal balance, TransactionCause cause) {
		Banker banker = get(player);
		banker.setBalance(balance, cause);
		save(banker);
	}

	@NotNull
	public static BigDecimal rounded(BigDecimal balance) {
		return balance.setScale(2, RoundingMode.HALF_EVEN);
	}

}
