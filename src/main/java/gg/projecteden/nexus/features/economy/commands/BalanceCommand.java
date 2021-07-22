package gg.projecteden.nexus.features.economy.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

@Aliases({"bal", "money"})
public class BalanceCommand extends CustomCommand {

	public BalanceCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
	}

	@Path("[player] [shopGroup]")
	void balance(@Arg("self") Banker banker, @Arg("current") ShopGroup shopGroup) {
		if (isSelf(banker))
			send(PREFIX + "Your " + camelCase(shopGroup) + " balance: &e" + banker.getBalanceFormatted(shopGroup));
		else
			send(PREFIX + "&e" + Nickname.of(banker) + "'s &3" + camelCase(shopGroup) + " balance: &e" + banker.getBalanceFormatted(shopGroup));
	}

}
