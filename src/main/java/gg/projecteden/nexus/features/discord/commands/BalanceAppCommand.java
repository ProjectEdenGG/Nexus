package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.Optional;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;

@Command("Check a player's balance")
public class BalanceAppCommand extends NexusAppCommand {

	public BalanceAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "Check a player's balance", literals = false)
	void check(
		@Desc("player") @Optional("self") Nerd player,
		@Desc("gamemode") @Optional("SURVIVAL") ShopGroup gamemode
	) {
		boolean isSelf = PlayerUtils.isSelf(player, verify());
		String formatted = new BankerService().getBalanceFormatted(player, gamemode);
		replyEphemeral(StringUtils.camelCase(gamemode) + " balance" + (isSelf ? "" : " of " + player.getNickname()) + ": " + formatted);
	}

}
