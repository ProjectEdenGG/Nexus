package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import static me.pugabyte.nexus.models.banker.Transaction.TransactionCause.shopCauses;
import static me.pugabyte.nexus.models.banker.Transaction.combine;
import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;
import static me.pugabyte.nexus.utils.StringUtils.shortishDateTimeFormat;
import static me.pugabyte.nexus.utils.StringUtils.timespanDiff;

@Aliases("txn")
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
		send(PREFIX + "History" + (isSelf(banker) ? "" : " for &e" + banker.getName()));

		BiFunction<Transaction, String, JsonBuilder> formatter = getFormatter(player(), banker);

		paginate(combine(transactions), formatter, "/transaction history " + banker.getName(), page);
	}

	@NotNull
	public static BiFunction<Transaction, String, JsonBuilder> getFormatter(Player player, Banker banker) {
		return (transaction, index) -> {
			String timestamp = shortishDateTimeFormat(transaction.getTimestamp());

			boolean deposit = transaction.isDeposit(banker.getUuid());
			boolean withdrawal = transaction.isWithdrawal(banker.getUuid());
			TransactionCause cause = transaction.getCause();

			String amount = prettyMoney(transaction.getAmount());
			String description = "";

			if (shopCauses.contains(cause)) {
				if (cause.name().contains("SALE"))
					description = "Sold";
				else if (cause.name().contains("PURCHASE"))
					description = "Purchased";

				if (cause == TransactionCause.SHOP_PURCHASE && deposit)
					description = "Sold";
				else if (cause == TransactionCause.SHOP_SALE && withdrawal)
					description = "Purchased";
				else if (cause == TransactionCause.MARKET_PURCHASE && deposit)
					description = "Sold";
				else if (cause == TransactionCause.MARKET_SALE && withdrawal)
					description = "Purchased";

				if (transaction.getDescription() != null)
					description += " ";

				description = "&3" + description;
			}

			if (transaction.getDescription() != null)
				description += "&e" + transaction.getDescription();

			// Deposit
			String fromPlayer = "&#dddddd" + getName(transaction.getSender(), cause);
			String toPlayer = PlayerUtils.isSelf(player, banker.getOfflinePlayer()) ? "&7&lYOU" : "&7" + banker.getName();
			String symbol = "&a+";
			String newBalance = prettyMoney(transaction.getReceiverNewBalance());

			// Withdrawal
			if (withdrawal) {
				symbol = "&c-";
				fromPlayer = PlayerUtils.isSelf(player, banker.getOfflinePlayer()) ? "&7&lYOU" : "&7" + banker.getName();
				toPlayer = "&#dddddd" + getName(transaction.getReceiver(), cause);
				newBalance = prettyMoney(transaction.getSenderNewBalance());
			}

			amount = symbol + amount;
			newBalance = "&e" + newBalance;

			JsonBuilder jsonBuilder = new JsonBuilder("&3" + index + " &7" + timestamp + "  " + newBalance + "  &7|  " +
					fromPlayer + " &3→ " + toPlayer + "  " + amount + "  " + description)
					.addHover("&3Time since: &e" + timespanDiff(transaction.getTimestamp()));

			if (PlayerUtils.isAdminGroup(player) && Nexus.isDebug())
				jsonBuilder
					.addHover("")
					.addHover(transaction.toString());

			return jsonBuilder;
		};
	}

	private static String getName(UUID uuid, TransactionCause cause) {
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
					return StringUtils.camelCase(cause);
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
