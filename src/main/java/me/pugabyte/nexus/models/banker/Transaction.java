package me.pugabyte.nexus.models.banker;

import com.mongodb.DBObject;
import dev.morphia.annotations.PreLoad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static me.pugabyte.nexus.models.banker.BankerService.rounded;
import static me.pugabyte.nexus.models.banker.Transaction.TransactionCause.shopCauses;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
		else if ("MARKET_BUY".equals(dbObject.get("cause")))
			dbObject.put("cause", "MARKET_PURCHASE");
		else if ("MARKET_SELL".equals(dbObject.get("cause")))
			dbObject.put("cause", "MARKET_SALE");
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
		this(receiver, newBalance, null, cause);
	}

	public Transaction(OfflinePlayer receiver, BigDecimal newBalance, String description, TransactionCause cause) {
		this.receiver = receiver.getUniqueId();
		this.receiverOldBalance = rounded(new BankerService().<Banker>get(receiver).getBalance());
		this.receiverNewBalance = rounded(newBalance);

		this.amount = rounded(this.receiverNewBalance.subtract(receiverOldBalance));
		this.description = description;
		this.cause = cause;
	}

	public boolean isDeposit(UUID uuid) {
		return !isWithdrawal(uuid);
	}

	public boolean isWithdrawal(UUID uuid) {
		return !uuid.equals(receiver);
	}

	public boolean isSimilar(Transaction transaction) {
		if (!Objects.equals(cause, transaction.getCause()))
			return false;

		if (!Objects.equals(receiver, transaction.getReceiver()))
			return false;
		if (!Objects.equals(sender, transaction.getSender()))
			return false;

		if (!Objects.equals(description, transaction.getDescription()))
			return false;

		if (shopCauses.contains(transaction.getCause()) && !Objects.equals(amount, transaction.getAmount()))
			return false;

		return true;
	}

	public Transaction clone() {
		return Transaction.builder()
				.receiver(receiver)
				.receiverOldBalance(clone(receiverOldBalance))
				.receiverNewBalance(clone(receiverNewBalance))
				.sender(sender)
				.senderOldBalance(clone(senderOldBalance))
				.senderNewBalance(clone(senderNewBalance))
				.amount(clone(amount))
				.description(description)
				.cause(cause)
				.timestamp(timestamp)
				.build();
	}

	private BigDecimal clone(BigDecimal amount) {
		if (amount == null)
			return null;
		return new BigDecimal(amount.toString());
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

		public static final List<TransactionCause> shopCauses = Arrays.asList(SHOP_SALE, SHOP_PURCHASE, MARKET_SALE, MARKET_PURCHASE);

		public Transaction of(OfflinePlayer sender, OfflinePlayer receiver, BigDecimal amount) {
			return of(sender, receiver, amount, null);
		}

		public Transaction of(OfflinePlayer sender, OfflinePlayer receiver, BigDecimal amount, String description) {
			return new Transaction(sender, receiver, amount, description, this);
		}

		public Transaction of(OfflinePlayer receiver, BigDecimal newBalance) {
			return of(receiver, newBalance, null);
		}

		public Transaction of(OfflinePlayer receiver, BigDecimal newBalance, String description) {
			return new Transaction(receiver, newBalance, description, this);
		}
	}

}
