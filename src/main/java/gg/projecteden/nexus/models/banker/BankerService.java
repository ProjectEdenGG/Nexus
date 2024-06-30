package gg.projecteden.nexus.models.banker;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Banker.class)
public class BankerService extends MongoPlayerService<Banker> {
	private static final Map<UUID, Banker> cache = new ConcurrentHashMap<>();

	public Map<UUID, Banker> getCache() {
		return cache;
	}

	public double getBalance(HasUniqueId player, ShopGroup shopGroup) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		return this.get(player).getBalance(shopGroup).doubleValue();
	}

	public String getBalanceFormatted(HasUniqueId player, ShopGroup shopGroup) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		return this.get(player).getBalanceFormatted(shopGroup);
	}

	public boolean has(HasUniqueId player, double money, ShopGroup shopGroup) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		return this.get(player).has(money, shopGroup);
	}

	public boolean has(HasUniqueId player, BigDecimal money, ShopGroup shopGroup) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		return this.get(player).has(money, shopGroup);
	}

	public void deposit(HasUniqueId player, double amount, ShopGroup shopGroup, TransactionCause cause) {
		deposit(player, BigDecimal.valueOf(amount), shopGroup, cause);
	}

	public void deposit(HasUniqueId player, BigDecimal money, ShopGroup shopGroup, TransactionCause cause) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.deposit(money, shopGroup, cause);
		queueSave(5, banker);
	}

	public void deposit(Transaction transaction) {
		deposit(transaction::getReceiver, transaction.getAmount(), transaction.getShopGroup(), transaction);
	}

	public void deposit(HasUniqueId player, BigDecimal money, ShopGroup shopGroup, Transaction transaction) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.deposit(money, shopGroup, transaction);
		queueSave(5, banker);
	}

	public void withdrawal(HasUniqueId player, double amount, ShopGroup shopGroup, TransactionCause cause) {
		withdrawal(player, BigDecimal.valueOf(amount), shopGroup, cause);
	}

	public void withdrawal(HasUniqueId player, BigDecimal money, ShopGroup shopGroup, TransactionCause cause) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.withdrawal(money, shopGroup, cause);
		queueSave(5, banker);
	}

	public void withdrawal(Transaction transaction) {
		withdrawal(transaction::getReceiver, transaction.getAmount(), transaction.getShopGroup(), transaction);
	}

	public void withdrawal(HasUniqueId player, BigDecimal money, ShopGroup shopGroup, Transaction transaction) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.withdrawal(money, shopGroup, transaction);
		queueSave(5, banker);
	}

	public void transfer(HasUniqueId from, HasUniqueId to, double amount, ShopGroup shopGroup, TransactionCause cause) {
		transfer(from, to, BigDecimal.valueOf(amount), shopGroup, cause);
	}

	public void transfer(HasUniqueId from, HasUniqueId to, BigDecimal money, ShopGroup shopGroup, TransactionCause cause) {
		transfer(get(from), get(to), money, shopGroup, cause);
	}

	public void transfer(Banker from, Banker to, BigDecimal money, ShopGroup shopGroup, TransactionCause cause) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		from.transfer(to, money, shopGroup, cause);
		queueSave(5, from);
		queueSave(5, to);
	}

	public void transfer(HasUniqueId from, HasUniqueId to, BigDecimal money, ShopGroup shopGroup, Transaction transaction) {
		transfer(get(from), get(to), money, shopGroup, transaction);
	}

	public void transfer(Banker from, Banker to, BigDecimal money, ShopGroup shopGroup, Transaction transaction) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		from.transfer(to, money, shopGroup, transaction);
		queueSave(5, from);
		queueSave(5, to);
	}

	public void setBalance(HasUniqueId player, double balance, ShopGroup shopGroup, TransactionCause cause) {
		setBalance(player, BigDecimal.valueOf(balance), shopGroup, cause);
	}

	public void setBalance(HasUniqueId player, BigDecimal balance, ShopGroup shopGroup, TransactionCause cause) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.setBalance(balance, shopGroup, cause);
		queueSave(5, banker);
	}

	public void setBalance(Transaction transaction) {
		setBalance(transaction::getReceiver, transaction.getAmount(), transaction.getShopGroup(), transaction);
	}

	public void setBalance(HasUniqueId player, BigDecimal balance, ShopGroup shopGroup, Transaction transaction) {
		Validate.notNull(shopGroup, "Shop Group cannot be null");
		Banker banker = get(player);
		banker.setBalance(balance, shopGroup, transaction);
		queueSave(5, banker);
	}

	@NotNull
	public static BigDecimal rounded(BigDecimal balance) {
		return balance.setScale(2, RoundingMode.HALF_EVEN);
	}

}
