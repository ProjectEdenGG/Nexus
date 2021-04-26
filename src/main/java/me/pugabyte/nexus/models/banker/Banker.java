package me.pugabyte.nexus.models.banker;

import com.mongodb.DBObject;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import dev.morphia.annotations.PreLoad;
import eden.mongodb.serializers.BigDecimalConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.economy.events.BalanceChangeEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.models.banker.BankerService.rounded;
import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;

@Data
@Builder
@Entity("banker")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, BigDecimalConverter.class})
public class Banker implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<ShopGroup, BigDecimal> balances = new HashMap<>();
	private List<Transaction> transactions = new ArrayList<>();

	@Deprecated
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private BigDecimal balance = BigDecimal.ZERO;

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
		return Nexus.isUUID0(uuid);
	}

	public String getBalanceFormatted(ShopGroup shopGroup) {
		return prettyMoney(getBalance(shopGroup));
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
		deposit(amount, shopGroup, cause.of(null, getOfflinePlayer(), amount, shopGroup));
	}

	void deposit(BigDecimal amount, ShopGroup shopGroup, Transaction transaction) {
		if (amount.signum() != 0)
			setBalance(getBalance(shopGroup).add(amount), shopGroup, transaction);
	}

	void withdraw(BigDecimal amount, ShopGroup shopGroup, TransactionCause cause) {
		withdraw(amount, shopGroup, cause.of(null, getOfflinePlayer(), amount, shopGroup));
	}

	void withdraw(BigDecimal amount, ShopGroup shopGroup, Transaction transaction) {
		if (amount.signum() != 0)
			setBalance(getBalance(shopGroup).subtract(amount), shopGroup, transaction);
	}

	void transfer(Banker to, BigDecimal amount, ShopGroup shopGroup, TransactionCause cause) {
		transfer(to, amount, shopGroup, cause.of(getOfflinePlayer(), to.getOfflinePlayer(), amount, shopGroup));
	}

	void transfer(Banker to, BigDecimal amount, ShopGroup shopGroup, Transaction transaction) {
		withdraw(amount, shopGroup, transaction);
		to.deposit(amount, shopGroup, transaction);
	}

	void setBalance(BigDecimal balance, ShopGroup shopGroup, TransactionCause cause) {
		setBalance(balance, shopGroup, cause.of(getOfflinePlayer(), balance, shopGroup));
	}

	void setBalance(BigDecimal balance, ShopGroup shopGroup, Transaction transaction) {
		if (isMarket())
			return;

		if (balance.signum() == -1)
			throw new NegativeBalanceException();

		BigDecimal newBalance = rounded(balance);

		if (new BalanceChangeEvent(getOfflinePlayer(), getBalance(shopGroup), newBalance, shopGroup).callEvent()) {
			transactions.add(transaction);
			balances.put(shopGroup, newBalance);
		}
	}

}
