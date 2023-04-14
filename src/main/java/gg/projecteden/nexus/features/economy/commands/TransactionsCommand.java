package gg.projecteden.nexus.features.economy.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.afk.events.NotAFKEvent;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.banker.Transactions;
import gg.projecteden.nexus.models.banker.TransactionsService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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

import static gg.projecteden.api.common.utils.TimeUtils.shortishDateTimeFormat;
import static gg.projecteden.api.common.utils.UUIDUtils.isUUID0;
import static gg.projecteden.api.common.utils.UUIDUtils.isV4Uuid;
import static gg.projecteden.nexus.models.banker.Transaction.TransactionCause.shopCauses;
import static gg.projecteden.nexus.models.banker.Transaction.combine;
import static gg.projecteden.nexus.utils.StringUtils.prettyMoney;

@NoArgsConstructor
@Aliases({"transaction", "txn", "txns"})
public class TransactionsCommand extends CustomCommand implements Listener {

	public TransactionsCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
	}

	@Async
	@Path("history [player] [page] [--world]")
	@Description("View recent transactions")
	void history(@Optional("self") Transactions banker, @Optional("1") int page, @Switch @Optional("current") ShopGroup world) {
		List<Transaction> transactions = banker.getTransactions().stream()
			.filter(transaction -> transaction.getShopGroup() == world)
			.sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
			.collect(Collectors.toList());

		if (transactions.isEmpty())
			error("&cNo transactions found in this world");

		send("");
		send(PREFIX + camelCase(world) + " transaction history" + (isSelf(banker) ? "" : " for &e" + banker.getNickname()));

		BiFunction<Transaction, String, JsonBuilder> formatter = getFormatter(player(), banker);

		paginate(combine(transactions), formatter, "/transaction history " + banker.getNickname() + " --world=" + world.name().toLowerCase(), page);
	}

	@Async
	@Path("volume [startTime] [endTime]")
	@Permission(Group.ADMIN)
	@Description("Sum all transaction volume")
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

	@Path("count [player]")
	@Description("Count the number of transactions stored on a player")
	@Permission(Group.ADMIN)
	void count(@Optional("self") Transactions banker) {
		send("Size: " + banker.getTransactions().size());
	}

	@NotNull
	public static BiFunction<Transaction, String, JsonBuilder> getFormatter(Player player, Transactions banker) {
		return (transaction, index) -> {
			String timestamp = shortishDateTimeFormat(transaction.getTimestamp());

			boolean deposit = transaction.isDeposit(banker.getUuid());
			boolean withdrawal = transaction.isWithdrawal(banker.getUuid());
			TransactionCause cause = transaction.getCause();

			String amount = prettyMoney(transaction.getAmount().abs());
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
				else if (cause == TransactionCause.DECORATION_STORE && withdrawal)
					description = "Purchased";
				else if (cause == TransactionCause.DECORATION_CATALOG && withdrawal)
					description = "Purchased";

				if (transaction.getDescription() != null)
					description += " ";

				description = "&3" + description;
			}

			if (transaction.getDescription() != null)
				description += "&e" + transaction.getDescription();

			// Deposit
			String fromPlayer = "&#dddddd" + getName(transaction.getSender(), cause);
			String toPlayer = PlayerUtils.isSelf(player, banker) ? "&7&lYOU" : "&7" + banker.getNickname();
			String symbol = "&a+";
			String newBalance = prettyMoney(transaction.getReceiverNewBalance());

			if (transaction.getAmount().signum() == -1) {
				symbol = "&c-";
				fromPlayer = PlayerUtils.isSelf(player, banker) ? "&7&lYOU" : "&7" + banker.getNickname();
				toPlayer = "&#dddddd" + getName(transaction.getSender(), cause);
				newBalance = prettyMoney(transaction.getReceiverNewBalance());
			} else if (withdrawal) {
				symbol = "&c-";
				fromPlayer = PlayerUtils.isSelf(player, banker) ? "&7&lYOU" : "&7" + banker.getNickname();
				toPlayer = "&#dddddd" + getName(transaction.getReceiver(), cause);
				newBalance = prettyMoney(transaction.getSenderNewBalance());
			}

			amount = symbol + amount;
			newBalance = "&e" + newBalance;

			JsonBuilder jsonBuilder = new JsonBuilder(index + " &7" + timestamp + "  " + newBalance + "  &7|  " +
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

		if (isV4Uuid(uuid))
			return Nickname.of(uuid);
		else if (isUUID0(uuid))
			return "Market";
		else
			return "Unknown";
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.waitAsync(TickTime.SECOND, () -> {
			if (!event.getPlayer().isOnline())
				return;

			final TransactionsService service = new TransactionsService();
			Transactions banker = service.get(event.getPlayer());

			final List<Transaction> transactions = banker.getUnreceivedTransactions();
			if (transactions.isEmpty())
				return;

			banker.sendMessage(json(EconomyCommand.PREFIX + "Transactions were made while you were offline, " +
					"&eclick here &3or use &c/txn history &3to view them").command("/txn history"));

			transactions.forEach(transaction -> transaction.setReceived(true));
			service.save(banker);
		});
	}

	@EventHandler
	public void on(NotAFKEvent event) {
		Tasks.waitAsync(TickTime.SECOND, () -> {
			if (!event.getUser().isOnline())
				return;

			final TransactionsService service = new TransactionsService();
			Transactions banker = service.get(event.getUser());

			final List<Transaction> transactions = banker.getUnreceivedTransactions();
			if (transactions.isEmpty())
				return;

			banker.sendMessage(json(EconomyCommand.PREFIX + "Transactions were made while you were AFK, " +
				"&eclick here &3or use &c/txn history &3to view them").command("/txn history"));

			transactions.forEach(transaction -> transaction.setReceived(true));
			service.save(banker);
		});
	}

}
