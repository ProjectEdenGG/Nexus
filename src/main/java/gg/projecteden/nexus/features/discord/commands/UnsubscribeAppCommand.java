package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.utils.DiscordId.Role;

@HandledBy(Bot.KODA)
public class UnsubscribeAppCommand extends NexusAppCommand {

	public UnsubscribeAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Subscribe to minigame updates")
	void minigames() {
		Discord.removeRole(member().getId(), Role.MINIGAME_NEWS);
		replyEphemeral("Unsubscribed from Minigame News");
	}

	@Command("Subscribe to minigame updates")
	void movienight() {
		Discord.removeRole(member().getId(), Role.MOVIE_GOERS);
		replyEphemeral("Unsubscribed from Movie Night");
	}

}
