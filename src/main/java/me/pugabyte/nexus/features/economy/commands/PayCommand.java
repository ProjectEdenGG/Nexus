package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NotEnoughMoneyException;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.utils.StringUtils;

import java.math.BigDecimal;

import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;

public class PayCommand extends CustomCommand {
	private final BankerService service = new BankerService();
	private final Banker self;

	public PayCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
		self = service.get(player());
	}

	@Path("<player> <amount>")
	void pay(Banker banker, @Arg(min = 0.01) BigDecimal amount) {
		if (isSelf(banker))
			error("You cannot pay yourself");

		try {
			service.transfer(self, banker, amount, TransactionCause.PAY);
		} catch (NegativeBalanceException ex) {
			throw new NotEnoughMoneyException();
		}

		send(PREFIX + "Sent &e" + prettyMoney(amount) + " &3to " + banker.getName());
		if (banker.isOnline())
			send(banker.getPlayer(), PREFIX + "Received &e" + prettyMoney(amount) + " &3from &e" + self.getName());
	}

}
