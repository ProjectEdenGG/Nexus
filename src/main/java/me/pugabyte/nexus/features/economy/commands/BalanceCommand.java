package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.StringUtils;

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
