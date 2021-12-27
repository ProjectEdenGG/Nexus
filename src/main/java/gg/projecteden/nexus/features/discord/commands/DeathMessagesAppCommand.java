package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Default;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.Optional;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.models.deathmessages.DeathMessages;
import gg.projecteden.nexus.models.deathmessages.DeathMessages.Behavior;
import gg.projecteden.nexus.models.deathmessages.DeathMessagesService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.TimeUtils.Timespan;

import static gg.projecteden.utils.StringUtils.camelCase;

@HandledBy(Bot.KODA)
public class DeathMessagesAppCommand extends NexusAppCommand {

	public DeathMessagesAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Change death message behavior")
	void behavior(
		@Desc("Behavior") Behavior behavior,
		@Desc("Player") @Optional @Default("self") @RequiredRole("Staff") DeathMessages deathMessages,
		@Desc("Duration") @Optional @Default("0") Timespan timespan
	) {
		deathMessages.setBehavior(behavior);
		if (!timespan.isNull())
			deathMessages.setExpiration(timespan.fromNow());

		new DeathMessagesService().save(deathMessages);

		replyEphemeral("%sSet %s's death message behavior to %s%s".formatted(StringUtils.getDiscordPrefix("DeathMessages"),
			deathMessages.getNickname(), camelCase(behavior), timespan.isNull() ? "" : " for " + timespan.format()));
	}

}
