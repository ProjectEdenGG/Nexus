package gg.projecteden.nexus.features.economy.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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

	@Path("[player] [--world]")
	void balance(@Arg("self") Banker banker, @Switch @Arg("current") ShopGroup world) {
		if (isSelf(banker))
			send(PREFIX + "Your " + camelCase(world) + " balance: &e" + banker.getBalanceFormatted(world));
		else
			send(PREFIX + "&e" + banker.getNickname() + "'s &3" + camelCase(world) + " balance: &e" + banker.getBalanceFormatted(world));
	}

}
