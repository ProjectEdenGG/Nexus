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
import me.pugabyte.nexus.utils.StringUtils;

import java.math.BigDecimal;

import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;

public class NexusPayCommand extends CustomCommand {
	private final BankerService service = new BankerService();
	private final Banker self;

	public NexusPayCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
		self = service.get(player());
	}

	@Path("<player> <amount>")
	void pay(Banker banker, @Arg(min = 0.01) BigDecimal amount) {
		if (isSelf(banker))
			error("You cannot pay yourself");

		try {
			self.withdraw(amount);
			banker.deposit(amount);
		} catch (NegativeBalanceException ex) {
			throw new NotEnoughMoneyException();
		}

		service.save(self);
		service.save(banker);

		send(PREFIX + "Sent &e" + prettyMoney(amount) + " &3to " + banker.getName());
	}

}
