package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.utils.DiscordId.Role;

@Command("Subscribe from roles")
public class SubscribeAppCommand extends NexusAppCommand {

	public SubscribeAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Subscribe to minigame updates")
	void minigames() {
		Discord.addRole(member().getId(), Role.MINIGAME_NEWS);
		replyEphemeral("Subscribed to Minigame News");
	}

	@Command("Subscribe to minigame updates")
	void movienight() {
		Discord.addRole(member().getId(), Role.MOVIE_GOERS);
		replyEphemeral("Subscribed to Movie Night");
	}

}
