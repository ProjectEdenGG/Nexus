package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.Transaction;
import me.pugabyte.nexus.utils.JsonBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

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
			return json("&3" + (index + 1) + " &e" + timestamp + " &7- &3" + transaction.toString())
					.addHover("&3Time since: &e" + timespanDiff(transaction.getTimestamp()));
		};

		paginate(transactions, formatter, "/transaction history " + banker.getName(), page);


	}

}
