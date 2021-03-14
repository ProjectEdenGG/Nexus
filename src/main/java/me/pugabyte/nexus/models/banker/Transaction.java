package me.pugabyte.nexus.models.banker;

import lombok.Data;
import me.pugabyte.nexus.Nexus;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Transaction {
	private final UUID receiver;
	private final BigDecimal receiverOldBalance;
	private final BigDecimal receiverNewBalance;

	private final UUID sender;
	private final BigDecimal senderOldBalance;
	private final BigDecimal senderNewBalance;

	private final BigDecimal amount;
	private final TransactionCause cause;

	public Transaction(OfflinePlayer receiver, OfflinePlayer sender, BigDecimal amount, TransactionCause cause) {
		this.receiver = receiver.getUniqueId();
		this.receiverOldBalance = new BankerService().<Banker>get(receiver).getBalance();
		this.receiverNewBalance = this.receiverOldBalance.add(amount);

		if (sender == null || Nexus.getUUID0().equals(sender.getUniqueId())) {
			this.sender = null;
			this.senderOldBalance = null;
			this.senderNewBalance = null;
		} else {
			this.sender = sender.getUniqueId();
			this.senderOldBalance = new BankerService().<Banker>get(sender).getBalance();
			this.senderNewBalance = this.senderOldBalance.add(amount);
		}

		this.amount = amount;
		this.cause = cause;
	}

	public enum TransactionCause {
		PAY,
		SHOP_SELL,
		SHOP_BUY,
		MARKET_SELL,
		MARKET_BUY,
		KILLER_MONEY,
		VOTE_POINT_STORE,
		DAILY_REWARD,
		VOTE_REWARD,
		MCMMO_RESET,
		EVENT,
		SERVER
	}

}
