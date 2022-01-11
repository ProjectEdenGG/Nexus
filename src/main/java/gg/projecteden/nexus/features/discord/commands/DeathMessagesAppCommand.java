package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Default;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.models.deathmessages.DeathMessages;
import gg.projecteden.nexus.models.deathmessages.DeathMessages.Behavior;
import gg.projecteden.nexus.models.deathmessages.DeathMessagesService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.TimeUtils.Timespan;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Command("Manage death messages")
public class DeathMessagesAppCommand extends NexusAppCommand {

	public DeathMessagesAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Change death message behavior")
	void behavior(
		@Desc("Behavior") Behavior behavior,
		@Desc("Player") @Default("self") @RequiredRole("Staff") DeathMessages player,
		@Desc("Duration") @Default("0") Timespan timespan
	) {
		player.setBehavior(behavior);
		if (!timespan.isNull())
			player.setExpiration(timespan.fromNow());

		new DeathMessagesService().save(player);

		replyEphemeral("%sSet %s's death message behavior to %s%s".formatted(StringUtils.getDiscordPrefix("DeathMessages"),
			player.getNickname(), camelCase(behavior), timespan.isNull() ? "" : " for " + timespan.format()));
	}

}
