package me.pugabyte.nexus.models.banker;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Banker.class)
public class BankerService extends MongoService<Banker> {
	private final static Map<UUID, Banker> cache = new HashMap<>();

	public Map<UUID, Banker> getCache() {
		return cache;
	}

	public double getBalance(OfflinePlayer player, ShopGroup shopGroup) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		return this.get(player).getBalance(shopGroup).doubleValue();
	}

	public String getBalanceFormatted(OfflinePlayer player, ShopGroup shopGroup) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		return this.get(player).getBalanceFormatted(shopGroup);
	}

	public boolean has(OfflinePlayer player, double money, ShopGroup shopGroup) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		return this.get(player).has(money, shopGroup);
	}

	public boolean has(OfflinePlayer player, BigDecimal money, ShopGroup shopGroup) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		return this.get(player).has(money, shopGroup);
	}

	public void deposit(OfflinePlayer player, double amount, ShopGroup shopGroup, TransactionCause cause) {
		deposit(player, BigDecimal.valueOf(amount), shopGroup, cause);
	}

	public void deposit(OfflinePlayer player, BigDecimal money, ShopGroup shopGroup, TransactionCause cause) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.deposit(money, shopGroup, cause);
		save(banker);
	}

	public void deposit(Transaction transaction) {
		deposit(Bukkit.getOfflinePlayer(transaction.getReceiver()), transaction.getAmount(), transaction.getShopGroup(), transaction);
	}

	public void deposit(OfflinePlayer player, BigDecimal money, ShopGroup shopGroup, Transaction transaction) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.deposit(money, shopGroup, transaction);
		save(banker);
	}

	public void withdraw(OfflinePlayer player, double amount, ShopGroup shopGroup, TransactionCause cause) {
		withdraw(player, BigDecimal.valueOf(amount), shopGroup, cause);
	}

	public void withdraw(OfflinePlayer player, BigDecimal money, ShopGroup shopGroup, TransactionCause cause) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.withdraw(money, shopGroup, cause);
		save(banker);
	}

	public void withdraw(Transaction transaction) {
		withdraw(Bukkit.getOfflinePlayer(transaction.getReceiver()), transaction.getAmount(), transaction.getShopGroup(), transaction);
	}

	public void withdraw(OfflinePlayer player, BigDecimal money, ShopGroup shopGroup, Transaction transaction) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.withdraw(money, shopGroup, transaction);
		save(banker);
	}

	public void transfer(OfflinePlayer from, OfflinePlayer to, double amount, ShopGroup shopGroup, TransactionCause cause) {
		transfer(from, to, BigDecimal.valueOf(amount), shopGroup, cause);
	}

	public void transfer(OfflinePlayer from, OfflinePlayer to, BigDecimal money, ShopGroup shopGroup, TransactionCause cause) {
		transfer(get(from), get(to), money, shopGroup, cause);
	}

	public void transfer(Banker from, Banker to, BigDecimal money, ShopGroup shopGroup, TransactionCause cause) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		from.transfer(to, money, shopGroup, cause);
		save(from);
		save(to);
	}

	public void transfer(OfflinePlayer from, OfflinePlayer to, BigDecimal money, ShopGroup shopGroup, Transaction transaction) {
		transfer(get(from), this.get(to), money, shopGroup, transaction);
	}

	public void transfer(Banker from, Banker to, BigDecimal money, ShopGroup shopGroup, Transaction transaction) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		from.transfer(to, money, shopGroup, transaction);
		save(from);
		save(to);
	}

	public void setBalance(OfflinePlayer player, double balance, ShopGroup shopGroup, TransactionCause cause) {
		setBalance(player, BigDecimal.valueOf(balance), shopGroup, cause);
	}

	public void setBalance(OfflinePlayer player, BigDecimal balance, ShopGroup shopGroup, TransactionCause cause) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.setBalance(balance, shopGroup, cause);
		save(banker);
	}

	public void setBalance(Transaction transaction) {
		setBalance(Bukkit.getOfflinePlayer(transaction.getReceiver()), transaction.getAmount(), transaction.getShopGroup(), transaction);
	}

	public void setBalance(OfflinePlayer player, BigDecimal balance, ShopGroup shopGroup, Transaction transaction) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.setBalance(balance, shopGroup, transaction);
		save(banker);
	}

	@NotNull
	public static BigDecimal rounded(BigDecimal balance) {
		return balance.setScale(2, RoundingMode.HALF_EVEN);
	}

}
