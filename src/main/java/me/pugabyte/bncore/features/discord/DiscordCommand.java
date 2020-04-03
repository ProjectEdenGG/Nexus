package me.pugabyte.bncore.features.discord;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class DiscordCommand extends CustomCommand {

	public DiscordCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send("https://discord.bnn.gg");
	}

}
