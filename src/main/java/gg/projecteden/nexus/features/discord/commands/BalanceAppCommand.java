package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Default;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.Optional;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.PlayerUtils;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Command("Check a player's balance")
public class BalanceAppCommand extends NexusAppCommand {

	public BalanceAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "Check a player's balance", literals = false)
	void check(
		@Desc("player") @Default("self") Nerd player,
		@Desc("gamemode") @Default("SURVIVAL") @Optional ShopGroup gamemode
	) {
		boolean isSelf = PlayerUtils.isSelf(player, verify());
		String formatted = new BankerService().getBalanceFormatted(player, gamemode);
		replyEphemeral(camelCase(gamemode) + " balance" + (isSelf ? "" : " of " + player.getNickname()) + ": " + formatted);
	}

}
