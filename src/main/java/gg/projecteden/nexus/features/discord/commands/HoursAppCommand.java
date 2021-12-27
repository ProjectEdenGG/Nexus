package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Default;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.discord.appcommands.annotations.Verify;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.utils.TimeUtils.Timespan.TimespanBuilder;

@HandledBy(Bot.KODA)
public class HoursAppCommand extends NexusAppCommand {

	public HoursAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Verify
	@Command(value = "Check a player's playtime", literals = false)
	void check(@Desc("Player") @Default("self") Hours hours) {
		String message = "**[Hours]** " + hours.getName() + "'s in-game playtime";
		message += System.lineSeparator() + "Total: **" + TimespanBuilder.of(hours.getTotal()).noneDisplay(true).format() + "**";
		message += System.lineSeparator() + "- Today: **" + TimespanBuilder.of(hours.getDaily()).noneDisplay(true).format() + "**";
		message += System.lineSeparator() + "- This month: **" + TimespanBuilder.of(hours.getMonthly()).noneDisplay(true).format() + "**";
		message += System.lineSeparator() + "- This year: **" + TimespanBuilder.of(hours.getYearly()).noneDisplay(true).format() + "**";

		replyEphemeral(message);
	}

}
