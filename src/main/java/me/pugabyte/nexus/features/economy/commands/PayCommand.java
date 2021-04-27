package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NotEnoughMoneyException;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.banker.Transactions;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.economy.commands.TransactionsCommand.getFormatter;
import static me.pugabyte.nexus.models.banker.Transaction.combine;
import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;

public class PayCommand extends CustomCommand {
	private final BankerService service = new BankerService();
	private final Banker self;

	public PayCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
		self = service.get(player());
	}

	@Path("<player> <amount> [shopGroup] [reason...]")
	void pay(Banker banker, @Arg(min = 0.01) BigDecimal amount, @Arg("current") ShopGroup shopGroup, String reason) {
		if (isSelf(banker))
			error("You cannot pay yourself");

		try {
			service.transfer(self, banker, amount, shopGroup, TransactionCause.PAY.of(player(), banker.getOfflinePlayer(), amount, shopGroup, reason));
		} catch (NegativeBalanceException ex) {
			throw new NotEnoughMoneyException();
		}

		String description = (reason == null ? "" : " &3for &e" + reason) + " &3in &e" + camelCase(shopGroup);
		send(PREFIX + "Sent &e" + prettyMoney(amount) + " &3to " + banker.getName() + description);
		if (banker.isOnline())
			send(banker.getPlayer(), PREFIX + "Received &e" + prettyMoney(amount) + " &3from &e" + self.getName() + description);
	}

	@Async
	@Path("history [player] [shopGroup] [page]")
	void history(@Arg("self") Transactions banker, @Arg("current") ShopGroup shopGroup, @Arg("1") int page) {
		List<Transaction> transactions = new ArrayList<>(banker.getTransactions()).stream()
				.filter(transaction -> transaction.getShopGroup() == shopGroup && transaction.getCause() == TransactionCause.PAY)
				.sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
				.collect(Collectors.toList());

		if (transactions.isEmpty())
			error("&cNo transactions found");

		send("");
		send(PREFIX + camelCase(shopGroup) + " history" + (isSelf(banker) ? "" : " for &e" + Nickname.of(banker)));

		BiFunction<Transaction, String, JsonBuilder> formatter = getFormatter(player(), banker);

		paginate(combine(transactions), formatter, "/pay history " + banker.getName() + " " + shopGroup.name().toLowerCase(), page);
	}

}
