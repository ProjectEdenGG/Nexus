package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.Optional;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.models.deathmessages.DeathMessages;
import gg.projecteden.nexus.models.deathmessages.DeathMessages.Behavior;
import gg.projecteden.nexus.models.deathmessages.DeathMessagesService;
import gg.projecteden.nexus.utils.StringUtils;

@Command("Manage death messages")
public class DeathMessagesAppCommand extends NexusAppCommand {

	public DeathMessagesAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Change death message behavior")
	void behavior(
		@Desc("Behavior") Behavior behavior,
		@Desc("Player") @Optional("self") @RequiredRole("Staff") DeathMessages player,
		@Desc("Duration") @Optional("0") Timespan timespan
	) {
		player.setBehavior(behavior);
		if (!timespan.isNull())
			player.setExpiration(timespan.fromNow());

		new DeathMessagesService().save(player);

		replyEphemeral("%sSet %s's death message behavior to %s%s".formatted(StringUtils.getDiscordPrefix("DeathMessages"),
			player.getNickname(), StringUtils.camelCase(behavior), timespan.isNull() ? "" : " for " + timespan.format()));
	}

}
