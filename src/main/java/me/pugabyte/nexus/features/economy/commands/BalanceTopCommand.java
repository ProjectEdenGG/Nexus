package me.pugabyte.nexus.features.economy.commands;

import dev.morphia.query.Sort;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;

import java.util.List;
import java.util.function.BiFunction;

@Aliases({"baltop", "moneytop"})
public class BalanceTopCommand extends CustomCommand {
	private final BankerService service = new BankerService();

	public BalanceTopCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
	}

	@Path("[page]")
	void baltop(@Arg(value = "1", min = 1) int page) {
		List<Banker> bankers = service.getAllSortedBy(Sort.descending("balance"));

		if (bankers.isEmpty())
			error("No balances found");

		double sum = bankers.stream().mapToDouble(banker -> banker.getBalance().doubleValue()).sum();

		send(PREFIX + "Top balances  &3|  Total: &e" + StringUtils.prettyMoney(sum));
		BiFunction<Banker, Integer, JsonBuilder> formatter = (banker, index) ->
				json("&3" + (index + 1) + " &e" + banker.getName() + " &7- " + banker.getBalanceFormatted());
		paginate(bankers, formatter, "/baltop", page);
	}

}
