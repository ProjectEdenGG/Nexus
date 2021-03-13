package me.pugabyte.nexus.models.banker;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
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
		Banker banker = get(player);
		return banker.getBalance().doubleValue();
	}

	public String getBalanceFormatted(OfflinePlayer player) {
		Banker banker = get(player);
		return banker.getBalanceFormatted();
	}

	public boolean has(OfflinePlayer player, double money) {
		Banker banker = get(player);
		return banker.has(money);
	}

	public boolean has(OfflinePlayer player, BigDecimal money) {
		Banker banker = get(player);
		return banker.has(money);
	}

	public void deposit(OfflinePlayer player, double money) {
		Banker banker = get(player);
		banker.deposit(money);
		save(banker);
	}

	public void deposit(OfflinePlayer player, BigDecimal money) {
		Banker banker = get(player);
		banker.deposit(money);
		save(banker);
	}

	public void withdraw(OfflinePlayer player, double money) {
		Banker banker = get(player);
		banker.withdraw(money);
		save(banker);
	}

	public void withdraw(OfflinePlayer player, BigDecimal money) {
		Banker banker = get(player);
		banker.withdraw(money);
		save(banker);
	}

	public void transfer(OfflinePlayer from, OfflinePlayer to, double money) {
		transfer(from, to, BigDecimal.valueOf(money));
	}

	public void transfer(OfflinePlayer from, OfflinePlayer to, BigDecimal money) {
		Banker bankerFrom = get(from);
		Banker bankerTo = get(to);
		bankerFrom.transfer(bankerTo, money);
		save(bankerFrom);
		save(bankerTo);
	}

	public void setBalance(OfflinePlayer player, double balance) {
		Banker banker = get(player);
		banker.setBalance(balance);
		save(banker);
	}

	public void setBalance(OfflinePlayer player, BigDecimal balance) {
		Banker banker = get(player);
		banker.setBalance(balance);
		save(banker);
	}

}

