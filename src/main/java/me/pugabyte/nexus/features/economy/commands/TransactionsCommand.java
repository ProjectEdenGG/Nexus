package me.pugabyte.nexus.features.economy.commands;

import eden.utils.TimeUtils.Time;
import eden.utils.TimeUtils.Timespan;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Transaction;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.banker.Transactions;
import me.pugabyte.nexus.models.banker.TransactionsService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static eden.utils.TimeUtils.shortishDateTimeFormat;
import static me.pugabyte.nexus.models.banker.Transaction.TransactionCause.shopCauses;
import static me.pugabyte.nexus.models.banker.Transaction.combine;
import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;

@NoArgsConstructor
@Aliases({"transaction", "txn", "txns"})
public class TransactionsCommand extends CustomCommand implements Listener {
	private ShopGroup shopGroup;

	public TransactionsCommand(@NonNull CommandEvent event) {
		super(event);
		shopGroup = ShopGroup.of(player());
		PREFIX = StringUtils.getPrefix("Economy");
	}

	@Async
	@Path("volume [startTime] [endTime]")
	void volume(LocalDateTime startTime, LocalDateTime endTime) {
		if (endTime == null)
			endTime = LocalDateTime.now();

		BigDecimal total = BigDecimal.valueOf(0);

		for (Transactions banker : new TransactionsService().getAll())
			for (Transaction transaction : banker.getTransactions()) {
				if (startTime != null && !transaction.getTimestamp().isAfter(startTime))
					continue;
				if (!transaction.getTimestamp().isBefore(endTime))
					continue;

				total = total.add(transaction.getAmount());
			}

		send(PREFIX + "Total volume" + (startTime != null ? " for " + Timespan.of(startTime, endTime).format() : "") + ": &e" + prettyMoney(total));
	}

	@Async
	@Path("history [player] [page]")
	void history(@Arg("self") Transactions banker, @Arg("1") int page) {
		List<Transaction> transactions = banker.getTransactions().stream()
				.filter(transaction -> transaction.getShopGroup() == shopGroup)
				.sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
				.collect(Collectors.toList());

		if (transactions.isEmpty())
			error("&cNo transactions found in this world");

		send("");
		send(PREFIX + camelCase(shopGroup) + " transaction history" + (isSelf(banker) ? "" : " for &e" + banker.getName()));

		BiFunction<Transaction, String, JsonBuilder> formatter = getFormatter(player(), banker);

		paginate(combine(transactions), formatter, "/transaction history " + banker.getName(), page);
	}

	@Path("count [player]")
	@Permission("group.admin")
	void count(Transactions banker) {
		send("Size: " + banker.getTransactions().size());
	}

	@NotNull
	public static BiFunction<Transaction, String, JsonBuilder> getFormatter(Player player, Transactions banker) {
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
			String toPlayer = PlayerUtils.isSelf(player, banker.getOfflinePlayer()) ? "&7&lYOU" : "&7" + Nickname.of(banker);
			String symbol = "&a+";
			String newBalance = prettyMoney(transaction.getReceiverNewBalance());

			// Withdrawal
			if (withdrawal) {
				symbol = "&c-";
				fromPlayer = PlayerUtils.isSelf(player, banker.getOfflinePlayer()) ? "&7&lYOU" : "&7" + Nickname.of(banker);
				toPlayer = "&#dddddd" + getName(transaction.getReceiver(), cause);
				newBalance = prettyMoney(transaction.getSenderNewBalance());
			}

			amount = symbol + amount;
			newBalance = "&e" + newBalance;

			JsonBuilder jsonBuilder = new JsonBuilder("&3" + index + " &7" + timestamp + "  " + newBalance + "  &7|  " +
					fromPlayer + " &3→ " + toPlayer + "  " + amount + "  " + description)
					.hover("&3Time since: &e" + Timespan.of(transaction.getTimestamp()).format());

			if (Rank.of(player).isAdmin() && Nexus.isDebug())
				jsonBuilder
					.hover("")
					.hover(transaction.toString());

			return jsonBuilder;
		};
	}

	private static String getName(UUID uuid, TransactionCause cause) {
		if (uuid == null) {
			return switch (cause) {
				case PAY, SHOP_SALE, SHOP_PURCHASE -> "Unknown";
				case MARKET_SALE, MARKET_PURCHASE -> "Market";
				default -> StringUtils.camelCase(cause);
			};
		}

		if (StringUtils.isV4Uuid(uuid))
			return Nickname.of(uuid);
		else if (StringUtils.isUUID0(uuid))
			return "Market";
		else
			return "Unknown";
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.waitAsync(Time.SECOND, () -> {
			if (!event.getPlayer().isOnline())
				return;

			Nerd nerd = Nerd.of(event.getPlayer());
			Transactions banker = new TransactionsService().get(event.getPlayer());

			if (banker.getTransactions().isEmpty())
				return;

			Transaction transaction = banker.getTransactions().get(banker.getTransactions().size() - 1);

			if (transaction.getTimestamp().isAfter(nerd.getLastQuit()))
				nerd.sendMessage(json(EconomyCommand.PREFIX + "Transactions were made while you were offline, " +
						"&eclick here &3or use &c/txn history &3to view them").command("/txn history"));
		});
	}

}
