package me.pugabyte.nexus.models.banker;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.economy.events.BalanceChangeEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.BigDecimalConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
public class Banker extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private BigDecimal balance = BigDecimal.valueOf(500);
	private List<Transaction> transactions = new ArrayList<>();

	public boolean isMarket() {
		return Nexus.isUUID0(uuid);
	}

	public String getBalanceFormatted() {
		return prettyMoney(balance);
	}

	public boolean has(double amount) {
		return has(BigDecimal.valueOf(amount));
	}

	public boolean has(BigDecimal amount) {
		return balance.compareTo(amount) >= 0;
	}

	void deposit(BigDecimal amount, TransactionCause cause) {
		deposit(amount, cause.of(null, getOfflinePlayer(), amount));
	}

	void deposit(BigDecimal amount, Transaction transaction) {
		setBalance(balance.add(amount), transaction);
	}

	void withdraw(BigDecimal amount, TransactionCause cause) {
		withdraw(amount, cause.of(null, getOfflinePlayer(), amount));
	}

	void withdraw(BigDecimal amount, Transaction transaction) {
		setBalance(balance.subtract(amount), transaction);
	}

	void transfer(Banker to, BigDecimal amount, TransactionCause cause) {
		transfer(to, amount, cause.of(getOfflinePlayer(), to.getOfflinePlayer(), amount));
	}

	void transfer(Banker to, BigDecimal amount, Transaction transaction) {
		withdraw(amount, transaction);
		to.deposit(amount, transaction);
	}

	void setBalance(BigDecimal balance, TransactionCause cause) {
		setBalance(balance, cause.of(getOfflinePlayer(), balance));
	}

	void setBalance(BigDecimal balance, Transaction transaction) {
		if (isMarket())
			return;

		if (balance.signum() == -1)
			throw new NegativeBalanceException();

		BigDecimal newBalance = rounded(balance);

		if (new BalanceChangeEvent(getOfflinePlayer(), this.balance, newBalance).callEvent()) {
			transactions.add(transaction);
			this.balance = newBalance;
		}
	}

}
