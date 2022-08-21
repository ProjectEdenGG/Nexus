package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.Optional;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.features.discord.commands.common.annotations.Verify;
import gg.projecteden.nexus.models.hours.Hours;

@Command("Check a player's playtime")
public class HoursAppCommand extends NexusAppCommand {

	public HoursAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Verify
	@Command(value = "Check a player's playtime", literals = false)
	void check(@Desc("Player") @Optional("self") Hours player) {
		String message = "**[Hours]** " + player.getNickname() + "'s in-game playtime";
		message += System.lineSeparator() + "Total: **" + TimespanBuilder.ofSeconds(player.getTotal()).noneDisplay(true).format() + "**";
		message += System.lineSeparator() + "- Today: **" + TimespanBuilder.ofSeconds(player.getDaily()).noneDisplay(true).format() + "**";
		message += System.lineSeparator() + "- This month: **" + TimespanBuilder.ofSeconds(player.getMonthly()).noneDisplay(true).format() + "**";
		message += System.lineSeparator() + "- This year: **" + TimespanBuilder.ofSeconds(player.getYearly()).noneDisplay(true).format() + "**";

		replyEphemeral(message);
	}

}
