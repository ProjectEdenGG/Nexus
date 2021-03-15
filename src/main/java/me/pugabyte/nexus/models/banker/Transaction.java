package me.pugabyte.nexus.models.banker;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static me.pugabyte.nexus.models.banker.BankerService.rounded;

@Data
@NoArgsConstructor
public class Transaction {
	private UUID receiver;
	private BigDecimal receiverOldBalance;
	private BigDecimal receiverNewBalance;

	private UUID sender = null;
	private BigDecimal senderOldBalance = null;
	private BigDecimal senderNewBalance = null;

	private BigDecimal amount;
	private String description;
	private TransactionCause cause;
	private LocalDateTime timestamp = LocalDateTime.now();

	public Transaction(@Nullable OfflinePlayer sender, @NotNull OfflinePlayer receiver, @NotNull BigDecimal amount, @NotNull TransactionCause cause) {
		this(sender, receiver, amount, null, cause);
	}
	// Add/subtract
	public Transaction(@Nullable OfflinePlayer sender, @NotNull OfflinePlayer receiver, @NotNull BigDecimal amount, @Nullable String description, @NotNull TransactionCause cause) {
		this.receiver = receiver.getUniqueId();
		this.receiverOldBalance = rounded(new BankerService().<Banker>get(receiver).getBalance());
		this.receiverNewBalance = rounded(this.receiverOldBalance.add(amount));

		if (sender != null && !Nexus.isUUID0(sender.getUniqueId())) {
			this.sender = sender.getUniqueId();
			this.senderOldBalance = rounded(new BankerService().<Banker>get(sender).getBalance());
			this.senderNewBalance = rounded(this.senderOldBalance.add(amount));
		}

		this.amount = amount;
		this.description = description;
		this.cause = cause;
	}

	// Set
	public Transaction(@NotNull OfflinePlayer receiver, @NotNull BigDecimal newBalance, @NotNull TransactionCause cause) {
		this.receiver = receiver.getUniqueId();
		this.receiverOldBalance = rounded(new BankerService().<Banker>get(receiver).getBalance());
		this.receiverNewBalance = rounded(newBalance);

		this.amount = rounded(this.receiverNewBalance.subtract(receiverOldBalance));
		this.cause = cause;
	}

	public enum TransactionCause {
		PAY,
		SHOP_SELL,
		SHOP_BUY,
		MARKET_SELL,
		MARKET_BUY,
		KILLER_MONEY,
		ANIMAL_TELEPORT_PEN,
		VOTE_POINT_STORE,
		DAILY_REWARD,
		VOTE_REWARD,
		MCMMO_RESET,
		EVENT,
		COUPON,
		SERVER;

		public Transaction of(@Nullable OfflinePlayer sender, @NotNull OfflinePlayer receiver, @NotNull BigDecimal amount) {
			return new Transaction(sender, receiver, amount, null, this);
		}

		public Transaction of(@Nullable OfflinePlayer sender, @NotNull OfflinePlayer receiver, @NotNull BigDecimal amount, String description) {
			return new Transaction(sender, receiver, amount, description, this);
		}

		public Transaction of(@NotNull OfflinePlayer receiver, @NotNull BigDecimal newBalance) {
			return new Transaction(receiver, newBalance, this);
		}
	}

}
