package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
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
import java.util.Arrays;
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

		if (transactions.isEmpty())
			error("&cNo transactions found");

		send("");
		send(PREFIX + "Transaction history" + (isSelf(banker) ? "" : " for &e" + banker.getName()));

		BiFunction<Transaction, Integer, JsonBuilder> formatter = (transaction, index) -> {
			String timestamp = shortDateTimeFormat(transaction.getTimestamp());

			String cost = prettyMoney(transaction.getAmount());
			String description = "";
			List<TransactionCause> shopCauses = Arrays.asList(TransactionCause.SHOP_SALE, TransactionCause.SHOP_PURCHASE, TransactionCause.MARKET_SALE, TransactionCause.MARKET_PURCHASE);
			if (shopCauses.contains(transaction.getCause())) {
				description += "&3" + StringUtils.camelCase(transaction.getCause());
				if (transaction.getDescription() != null)
					description += ": &e" + transaction.getDescription();
			}

			// Deposit
			String fromPlayer = "&#dddddd" + getName(transaction.getSender(), transaction.getCause());
			String toPlayer = isSelf(banker) ? "&7&lYOU" : "&7" + banker.getName();
			String symbol = "&a+";
			String newBalance = prettyMoney(transaction.getReceiverNewBalance());

			// Withdrawal
			if (!banker.getUuid().equals(transaction.getReceiver())) {
				symbol = "&c-";
				fromPlayer = isSelf(banker) ? "&7&lYOU" : "&7" + banker.getName();
				toPlayer = "&#dddddd" + getName(transaction.getReceiver(), transaction.getCause());
				newBalance = prettyMoney(transaction.getSenderNewBalance());
				description = description.replace("Sale", "Purchase");
			}

			cost = symbol + cost;
			newBalance = "&e" + newBalance;

			return json("&3" + (index + 1) + " &7" + timestamp + "  " + newBalance + "  &7|  " +
					fromPlayer + " &3→ " + toPlayer + "  " + cost + "  " + description)
					.addHover("&3Time since: &e" + timespanDiff(transaction.getTimestamp()))
					.addHover("")
					.addHover(transaction.toString());
		};

		paginate(transactions, formatter, "/transaction history " + banker.getName(), page);
	}

	// fix UUID0
	private String getName(UUID uuid, TransactionCause cause) {
		if (uuid == null) {
			switch (cause) {
				case PAY:
				case SHOP_SALE:
				case SHOP_PURCHASE:
					return "Unknown";
				case MARKET_SALE:
				case MARKET_PURCHASE:
					return "Market";
				default:
					return camelCase(cause);
			}
		}

		if (StringUtils.isV4Uuid(uuid))
			return PlayerUtils.getPlayer(uuid).getName();
		else if (Nexus.isUUID0(uuid))
			return "Market";
		else
			return "Unknown";
	}

}
