package gg.projecteden.nexus.features.economy.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NotEnoughMoneyException;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.banker.Transactions;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PayCommand extends CustomCommand {
	private final BankerService service = new BankerService();
	private final Banker self;

	public PayCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
		self = service.get(player());
	}

	@Path("<player> <amount> [reason...] [--world]")
	@Description("Send a player money")
	void pay(Banker banker, @Arg(min = 0.01) BigDecimal amount, String reason, @Switch @Arg("current") ShopGroup world) {
		if (isSelf(banker))
			error("You cannot pay yourself");

		try {
			service.transfer(self, banker, amount, world, TransactionCause.PAY.of(player(), banker, amount, world, reason));
		} catch (NegativeBalanceException ex) {
			throw new NotEnoughMoneyException();
		}

		String description = (reason == null ? "" : " &3for &e" + reason) + " &3in &e" + camelCase(world);
		send(PREFIX + "Sent &e" + StringUtils.prettyMoney(amount) + " &3to " + banker.getNickname() + description);
		if (banker.isOnline())
			send(banker.getOnlinePlayer(), PREFIX + "Received &e" + StringUtils.prettyMoney(amount) + " &3from &e" + self.getNickname() + description);
	}

	@Async
	@Path("history [player] [page] [--world]")
	@Description("View recent payment transactions")
	void history(@Arg("self") Transactions banker, @Arg("1") int page, @Switch @Arg("current") ShopGroup world) {
		List<Transaction> transactions = new ArrayList<>(banker.getTransactions()).stream()
			.filter(transaction -> transaction.getShopGroup() == world && transaction.getCause() == TransactionCause.PAY)
			.sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
			.collect(Collectors.toList());

		if (transactions.isEmpty())
			error("&cNo transactions found");

		send("");
		send(PREFIX + camelCase(world) + " history" + (isSelf(banker) ? "" : " for &e" + banker.getNickname()));

		BiFunction<Transaction, String, JsonBuilder> formatter = TransactionsCommand.getFormatter(player(), banker);

		paginate(Transaction.combine(transactions), formatter, "/pay history " + banker.getNickname() + " --world=" + world.name().toLowerCase(), page);
	}

}
