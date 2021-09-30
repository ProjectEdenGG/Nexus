package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.features.socialmedia.integrations.Twitch;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import lombok.NonNull;

public class TwitchCommand extends CustomCommand {

	public TwitchCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Twitch.connect();
	}

	@Path("status <user>")
	void status(DiscordUser user) {
		final boolean streaming = Twitch.isStreaming(user);
		send(PREFIX + "User " + (streaming ? "&ais" : "is &cnot") + " &3streaming");
	}

	@Async
	@Path("api status <user>")
	void status(SocialMediaUser user) {
		Twitch.isStreaming(user).thenAccept(streaming -> send(PREFIX + "User " + (streaming ? "&ais" : "is &cnot") + " &3streaming"));
	}

}
