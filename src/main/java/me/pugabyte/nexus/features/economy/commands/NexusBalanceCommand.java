package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.utils.StringUtils;

//@Aliases({"bal", "money"})
public class NexusBalanceCommand extends CustomCommand {

	public NexusBalanceCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
	}

	@Path("[player]")
	void balance(@Arg("self") Banker banker) {
		if (isSelf(banker))
			send(PREFIX + "Your balance: &e" + banker.getBalanceFormatted());
		else
			send(PREFIX + banker.getName() + "'s balance: " + banker.getBalanceFormatted());
	}

}
