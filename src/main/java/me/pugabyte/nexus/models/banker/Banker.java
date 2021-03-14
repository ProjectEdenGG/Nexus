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
import me.pugabyte.nexus.features.economy.events.BalanceChangeEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.BigDecimalConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

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

	public String getBalanceFormatted() {
		return prettyMoney(balance);
	}

	public boolean has(double money) {
		return has(BigDecimal.valueOf(money));
	}

	public boolean has(BigDecimal money) {
		return balance.compareTo(money) >= 0;
	}

	public void deposit(double money) {
		deposit(BigDecimal.valueOf(money));
	}

	public void deposit(BigDecimal money) {
		setBalance(balance.add(money));
	}

	public void withdraw(double money) {
		withdraw(BigDecimal.valueOf(money));
	}

	public void withdraw(BigDecimal money) {
		setBalance(balance.subtract(money));
	}

	public void transfer(OfflinePlayer from, double amount) {
		transfer(from, BigDecimal.valueOf(amount));
	}

	public void transfer(OfflinePlayer from, BigDecimal amount) {
		transfer(new BankerService().<Banker>get(from), amount);
	}

	public void transfer(Banker to, BigDecimal amount) {
		withdraw(amount);
		to.deposit(amount);
	}

	public void setBalance(double balance) {
		setBalance(BigDecimal.valueOf(balance));
	}

	public void setBalance(BigDecimal balance) {
		if (balance.signum() == -1)
			throw new NegativeBalanceException();

		BigDecimal newBalance = balance.setScale(2, RoundingMode.HALF_EVEN);

		if (new BalanceChangeEvent(getOfflinePlayer(), this.balance, newBalance).callEvent())
			this.balance = newBalance;
	}

}
