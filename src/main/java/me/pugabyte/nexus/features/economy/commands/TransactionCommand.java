package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.Transaction;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;
import static me.pugabyte.nexus.utils.StringUtils.shortDateTimeFormat;
import static me.pugabyte.nexus.utils.StringUtils.timespanDiff;

public class TransactionCommand extends CustomCommand {

	public TransactionCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("history [player] [page]")
	void history(@Arg("self") Banker banker, @Arg("1") int page) {
		List<Transaction> transactions = new ArrayList<>(banker.getTransactions());
		transactions.sort(Comparator.comparing(Transaction::getTimestamp).reversed());

		BiFunction<Transaction, Integer, JsonBuilder> formatter = (transaction, index) -> {
			String timestamp = shortDateTimeFormat(transaction.getTimestamp());

			String cost = prettyMoney(transaction.getAmount());
			String cause = "";
			String description = "";
			if (!transaction.getCause().equals(TransactionCause.PAY)) {
				cause = "&3, " + StringUtils.camelCase(transaction.getCause());
				if (transaction.getDescription() != null)
					description = "&3: &e" + transaction.getDescription();
			}

			// Deposit
			String fromPlayer = getName(transaction.getSender(), transaction.getCause());
			String toPlayer = "&7&lYOU";
			String symbol = "&a+";
			String newBalance = prettyMoney(transaction.getReceiverNewBalance());

			// Withdrawal
			if (!transaction.getReceiver().equals(banker.getUuid())) {
				symbol = "&c-";
				fromPlayer = "&7&lYOU";
				toPlayer = getName(transaction.getReceiver(), transaction.getCause());
				newBalance = prettyMoney(transaction.getSenderNewBalance());
			}

			return json("&3" + (index + 1) + " &e" + timestamp + " &7- " +
					"&e" + fromPlayer + " &3→&e " + toPlayer + "&3, " + symbol + cost + "&3, &e" + newBalance + cause + description)
					.addHover("&3Time since: &e" + timespanDiff(transaction.getTimestamp()));
		};

		paginate(transactions, formatter, "/transaction history " + banker.getName(), page);


	}

	// fix UUID0
	private String getName(UUID uuid, TransactionCause cause) {
		if (uuid == null) {
			switch (cause) {
				case PAY:
				case SHOP_SELL:
				case SHOP_BUY:
					return "Unknown";
				default:
					return "Server";
			}
		}

		return PlayerUtils.getPlayer(uuid).getName();
	}

}
