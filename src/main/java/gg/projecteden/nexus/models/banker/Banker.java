package gg.projecteden.nexus.models.banker;

import com.mongodb.DBObject;
import dev.morphia.annotations.*;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.BigDecimalConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.economy.events.BalanceChangeEvent;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "banker", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, BigDecimalConverter.class})
public class Banker implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<ShopGroup, BigDecimal> balances = new ConcurrentHashMap<>();

	@Deprecated
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private BigDecimal balance = BigDecimal.ZERO;

	public static Banker of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static Banker of(HasUniqueId player) {
		return of(player.getUniqueId());
	}

	public static Banker of(UUID uuid) {
		return new BankerService().get(uuid);
	}

	@PreLoad
	void fixPreLoad(DBObject dbObject) {
		DBObject map = (DBObject) dbObject.get("balances");
		if (map != null && map.containsField("RESOURCE"))
			map.removeField("RESOURCE");
	}

	@PostLoad
	void fixPostLoad(DBObject dbObject) {
		if (balance.signum() == 1) {
			balances.put(ShopGroup.SURVIVAL, new BigDecimal(balance.toString()));
			balance = BigDecimal.ZERO;
		}
	}

	public boolean isMarket() {
		return UUIDUtils.isUUID0(uuid);
	}

	public String getBalanceFormatted(ShopGroup shopGroup) {
		return StringUtils.prettyMoney(getBalance(shopGroup));
	}

	public BigDecimal getBalance(ShopGroup shopGroup) {
		return balances.getOrDefault(shopGroup, BigDecimal.valueOf(500));
	}

	public boolean has(double amount, ShopGroup shopGroup) {
		return has(BigDecimal.valueOf(amount), shopGroup);
	}

	public boolean has(BigDecimal amount, ShopGroup shopGroup) {
		return getBalance(shopGroup).compareTo(amount) >= 0;
	}

	void deposit(BigDecimal amount, ShopGroup shopGroup, TransactionCause cause) {
		deposit(amount, shopGroup, cause.of(null, this, amount, shopGroup));
	}

	void deposit(BigDecimal amount, ShopGroup shopGroup, Transaction transaction) {
		if (amount.signum() != 0)
			setBalance(getBalance(shopGroup).add(amount), shopGroup, transaction);
	}

	void withdraw(BigDecimal amount, ShopGroup shopGroup, TransactionCause cause) {
		withdraw(amount, shopGroup, cause.of(null, this, amount, shopGroup));
	}

	void withdraw(BigDecimal amount, ShopGroup shopGroup, Transaction transaction) {
		if (amount.signum() != 0)
			setBalance(getBalance(shopGroup).add(amount), shopGroup, transaction);
	}

	void transfer(Banker to, BigDecimal amount, ShopGroup shopGroup, TransactionCause cause) {
		transfer(to, amount, shopGroup, cause.of(this, to, amount, shopGroup));
	}

	void transfer(Banker to, BigDecimal amount, ShopGroup shopGroup, Transaction transaction) {
		withdraw(amount.multiply(new BigDecimal(-1)), shopGroup, transaction);
		to.deposit(amount, shopGroup, transaction);
	}

	void setBalance(BigDecimal balance, ShopGroup shopGroup, TransactionCause cause) {
		setBalance(balance, shopGroup, cause.of(this, balance, shopGroup));
	}

	void setBalance(BigDecimal balance, ShopGroup shopGroup, Transaction transaction) {
		if (isMarket())
			return;

		if (balance.signum() == -1)
			throw new NegativeBalanceException();

		BigDecimal newBalance = BankerService.rounded(balance);
		BigDecimal difference = newBalance.subtract(getBalance(shopGroup));

		if (new BalanceChangeEvent(uuid, getBalance(shopGroup), newBalance, shopGroup).callEvent()) {
			TransactionsService transactionsService = new TransactionsService();
			Transactions transactions = transactionsService.get(this);

			if (Nerd.of(uuid).isOnline() && AFK.get(uuid).isNotTimeAfk())
				transaction.setReceived(true);

			transactions.getTransactions().add(transaction);
			transactionsService.queueSave(5, transactions);
			balances.put(shopGroup, newBalance);

			updateActionBar(difference);
		}
	}

	private transient BigDecimal profit = new BigDecimal(0);
	private transient int taskId = 0;

	private void updateActionBar(BigDecimal change) {
		try {
			if (!isOnline())
				return;

			if (profit == null)
				profit = new BigDecimal(0);

			profit = profit.add(change);

			if (profit.signum() != 0) {
				Tasks.cancel(taskId);
				final String message = (profit.signum() > 0 ? "&a+" : "&c") + StringUtils.prettyMoney(profit);
				ActionBarUtils.sendActionBar(getOnlinePlayer(), message);
				taskId = Tasks.wait(TickTime.SECOND.x(3.5), () -> profit = new BigDecimal(0));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
