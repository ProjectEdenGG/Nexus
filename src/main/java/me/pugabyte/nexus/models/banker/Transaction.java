package me.pugabyte.nexus.models.banker;

import com.mongodb.DBObject;
import dev.morphia.annotations.PreLoad;
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
	private UUID receiver = null;
	private BigDecimal receiverOldBalance = null;
	private BigDecimal receiverNewBalance = null;

	private UUID sender = null;
	private BigDecimal senderOldBalance = null;
	private BigDecimal senderNewBalance = null;

	private BigDecimal amount;
	private String description;
	private TransactionCause cause;
	private LocalDateTime timestamp = LocalDateTime.now();

	@PreLoad
	void fix(DBObject dbObject) {
		if ("SHOP_BUY".equals(dbObject.get("cause")))
			dbObject.put("cause", "SHOP_PURCHASE");
		else if ("SHOP_SELL".equals(dbObject.get("cause")))
			dbObject.put("cause", "SHOP_SALE");
	}

	// Add/subtract
	public Transaction(OfflinePlayer sender, OfflinePlayer receiver, BigDecimal amount, TransactionCause cause) {
		this(sender, receiver, amount, null, cause);
	}

	public Transaction(OfflinePlayer sender, OfflinePlayer receiver, BigDecimal amount, String description, TransactionCause cause) {
		if (receiver != null && !Nexus.isUUID0(receiver.getUniqueId())) {
			this.receiver = receiver.getUniqueId();
			this.receiverOldBalance = rounded(new BankerService().<Banker>get(receiver).getBalance());
			this.receiverNewBalance = rounded(this.receiverOldBalance.add(amount));
		}

		if (sender != null && !Nexus.isUUID0(sender.getUniqueId())) {
			this.sender = sender.getUniqueId();
			this.senderOldBalance = rounded(new BankerService().<Banker>get(sender).getBalance());
			this.senderNewBalance = rounded(this.senderOldBalance.subtract(amount));
		}

		this.amount = amount;
		this.description = description;
		this.cause = cause;
	}

	// Set
	public Transaction(OfflinePlayer receiver, BigDecimal newBalance, TransactionCause cause) {
		this.receiver = receiver.getUniqueId();
		this.receiverOldBalance = rounded(new BankerService().<Banker>get(receiver).getBalance());
		this.receiverNewBalance = rounded(newBalance);

		this.amount = rounded(this.receiverNewBalance.subtract(receiverOldBalance));
		this.cause = cause;
	}

	public enum TransactionCause {
		PAY,
		SHOP_SALE,
		SHOP_PURCHASE,
		MARKET_SALE,
		MARKET_PURCHASE,
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
