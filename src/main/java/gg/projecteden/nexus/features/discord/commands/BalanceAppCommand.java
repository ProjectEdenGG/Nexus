package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Default;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.Optional;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.PlayerUtils;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@HandledBy(Bot.KODA)
public class BalanceAppCommand extends NexusAppCommand {

	public BalanceAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "Check a player's balance", literals = false)
	void check(
		@Desc("player") @Default("self") Nerd nerd,
		@Desc("gamemode") @Default("SURVIVAL") @Optional ShopGroup shopGroup
	) {
		boolean isSelf = PlayerUtils.isSelf(nerd, verify());
		String formatted = new BankerService().getBalanceFormatted(nerd, shopGroup);
		replyEphemeral(camelCase(shopGroup) + " balance" + (isSelf ? "" : " of " + nerd.getNickname()) + ": " + formatted);
	}

}
