package gg.projecteden.nexus.features.economy.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

@Aliases({"bal", "money"})
public class BalanceCommand extends CustomCommand {

	public BalanceCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
	}

	@NoLiterals
	@Path("[player] [--world]")
	@Description("View your or another player's balance in a world")
	void balance(@Optional("self") Banker banker, @Switch @Optional("current") ShopGroup world) {
		if (isSelf(banker))
			send(PREFIX + "Your " + camelCase(world) + " balance: &e" + banker.getBalanceFormatted(world));
		else
			send(PREFIX + "&e" + banker.getNickname() + "'s &3" + camelCase(world) + " balance: &e" + banker.getBalanceFormatted(world));
	}

}
