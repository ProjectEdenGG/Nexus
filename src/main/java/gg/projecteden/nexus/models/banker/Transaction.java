package gg.projecteden.nexus.models.banker;

import com.mongodb.DBObject;
import com.mysql.cj.util.StringUtils;
import dev.morphia.annotations.PreLoad;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
	private UUID receiver;
	private BigDecimal receiverOldBalance;
	private BigDecimal receiverNewBalance;

	private UUID sender;
	private BigDecimal senderOldBalance;
	private BigDecimal senderNewBalance;

	private BigDecimal amount;
	private String description;
	private TransactionCause cause;
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();
	private ShopGroup shopGroup;

	private boolean received;

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

		if ("RESOURCE".equals(dbObject.get("shopGroup")))
			dbObject.put("shopGroup", ShopGroup.SURVIVAL);
	}

	// Add/subtract
	public Transaction(HasUniqueId sender, HasUniqueId receiver, BigDecimal amount, ShopGroup shopGroup, TransactionCause cause) {
		this(sender, receiver, amount, shopGroup, null, cause);
	}

	public Transaction(HasUniqueId sender, HasUniqueId receiver, BigDecimal amount, ShopGroup shopGroup, String description, TransactionCause cause) {
		if (receiver != null && !UUIDUtils.isUUID0(receiver.getUniqueId())) {
			this.receiver = receiver.getUniqueId();
			this.receiverOldBalance = BankerService.rounded(new BankerService().get(receiver).getBalance(shopGroup));
			this.receiverNewBalance = BankerService.rounded(this.receiverOldBalance.add(amount));
		}

		if (sender != null && !UUIDUtils.isUUID0(sender.getUniqueId())) {
			this.sender = sender.getUniqueId();
			this.senderOldBalance = BankerService.rounded(new BankerService().get(sender).getBalance(shopGroup));
			this.senderNewBalance = BankerService.rounded(this.senderOldBalance.subtract(amount));
		}

		this.amount = amount;
		this.description = description;
		this.cause = cause;
		this.shopGroup = shopGroup;
		this.timestamp = LocalDateTime.now();

		if (shopGroup == null)
			throw new InvalidInputException("Could not determine shop group for transaction");
	}

	// Set
	public Transaction(HasUniqueId receiver, BigDecimal newBalance, ShopGroup shopGroup, TransactionCause cause) {
		this(receiver, newBalance, shopGroup, null, cause);
	}

	public Transaction(HasUniqueId receiver, BigDecimal newBalance, ShopGroup shopGroup, String description, TransactionCause cause) {
		this.receiver = receiver.getUniqueId();
		this.receiverOldBalance = BankerService.rounded(new BankerService().get(receiver).getBalance(shopGroup));
		this.receiverNewBalance = BankerService.rounded(newBalance);

		this.amount = BankerService.rounded(this.receiverNewBalance.subtract(receiverOldBalance));
		this.description = description;
		this.cause = cause;
		this.shopGroup = shopGroup;
		this.timestamp = LocalDateTime.now();

		if (shopGroup == null)
			throw new InvalidInputException("Could not determine shop group for transaction");
	}

	public boolean isDeposit(UUID uuid) {
		return !isWithdrawal(uuid);
	}

	public boolean isWithdrawal(UUID uuid) {
		return !uuid.equals(receiver) || amount.signum() == -1;
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

		if (TransactionCause.shopCauses.contains(transaction.getCause()) && !Objects.equals(amount, transaction.getAmount()))
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

	public static List<Transaction> combine(List<Transaction> transactions) {
		List<Transaction> combined = new ArrayList<>();

		Transaction previous = null;
		BigDecimal combinedAmount = BigDecimal.ZERO;
		int count = 0;

		for (Transaction transaction : transactions) {
			if (previous == null) {
				previous = transaction.clone();
				combinedAmount = new BigDecimal(previous.getAmount().toString());
				count = calculateCount(previous, transaction);
				continue;
			}

			if (previous.isSimilar(transaction)) {
				combinedAmount = combinedAmount.add(transaction.getAmount());
				count += calculateCount(previous, transaction);
			} else {
				previous.setAmount(combinedAmount);
				if (count > 0)
					previous.setDescription(count + " " + previous.getDescription().split(" ", 2)[1]);
				combined.add(previous);

				previous = transaction.clone();
				combinedAmount = new BigDecimal(previous.getAmount().toString());
				count = calculateCount(previous, transaction);
			}
		}

		if (previous != null) {
			previous.setAmount(combinedAmount);
			if (count > 0)
				previous.setDescription(count + " " + previous.getDescription().split(" ", 2)[1]);
			combined.add(previous);
		}

		return combined;
	}

	private static int calculateCount(Transaction previous, Transaction transaction) {
		if (!TransactionCause.shopCauses.contains(previous.getCause()))
			return 0;

		if (StringUtils.isNullOrEmpty(transaction.getDescription()))
			return 0;

		String[] split = transaction.getDescription().split(" ", 2);
		if (split.length != 2)
			return 0;

		if (!Utils.isInt(split[0]))
			return 0;

		return Integer.parseInt(split[0]);
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
		SERVER,
		ACCOUNT_TRANSFER,
		CRATES,
		DECORATION_STORE,
		DECORATION_CATALOG,
		MGM_TOKEN_EXCHANGE,
		CHAT_GAME,
		;

		public static final List<TransactionCause> shopCauses = List.of(
			SHOP_SALE, SHOP_PURCHASE, MARKET_SALE, MARKET_PURCHASE, DECORATION_STORE, DECORATION_CATALOG
		);

		public Transaction of(HasUniqueId sender, HasUniqueId receiver, BigDecimal amount, ShopGroup shopGroup) {
			return of(sender, receiver, amount, shopGroup, null);
		}

		public Transaction of(HasUniqueId sender, HasUniqueId receiver, BigDecimal amount, ShopGroup shopGroup, String description) {
			return new Transaction(sender, receiver, amount, shopGroup, description, this);
		}

		public Transaction of(HasUniqueId receiver, BigDecimal newBalance, ShopGroup shopGroup) {
			return of(receiver, newBalance, shopGroup, null);
		}

		public Transaction of(HasUniqueId receiver, BigDecimal newBalance, ShopGroup shopGroup, String description) {
			return new Transaction(receiver, newBalance, shopGroup, description, this);
		}
	}

}
