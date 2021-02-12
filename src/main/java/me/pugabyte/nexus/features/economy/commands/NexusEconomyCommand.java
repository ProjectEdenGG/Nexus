package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.utils.StringUtils;

import java.math.BigDecimal;

import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;

//@Aliases("eco")
public class NexusEconomyCommand extends CustomCommand {

	public NexusEconomyCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
	}

	@Path("set <player> <balance>")
	void set(Banker banker, BigDecimal balance) {
		banker.setBalance(balance);
		send(PREFIX + "Set &e" + banker.getName() + "'s &3balance to &e" + banker.getBalanceFormatted());
	}

	@Path("give <player> <balance>")
	void give(Banker banker, BigDecimal balance) {
		banker.deposit(balance);
		send(PREFIX + "Added &e" + prettyMoney(balance) + " &3to &e" + banker.getName() + "'s &3balance. New balance: &e" + banker.getBalanceFormatted());
	}

	@Path("take <player> <balance>")
	void take(Banker banker, BigDecimal balance) {
		banker.withdraw(balance);
		send(PREFIX + "Removed &e" + prettyMoney(balance) + " &3from &e" + banker.getName() + "'s &3balance. New balance: &e" + banker.getBalanceFormatted());
	}

}
