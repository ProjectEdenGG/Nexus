package me.pugabyte.bncore.features.chat.commands;


import lombok.NonNull;
import me.pugabyte.bncore.features.chat.Censor;
import me.pugabyte.bncore.features.chat.events.ChatEvent;
import me.pugabyte.bncore.features.chat.events.PublicChatEvent;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.PublicChannel;

import java.util.HashSet;

@Permission("group.seniorstaff")
public class CensorCommand extends CustomCommand {

	public CensorCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("process <channel> <message...>")
	void process(PublicChannel channel, String message) {
		ChatEvent event = new PublicChatEvent(new ChatService().get(player()), channel, message, new HashSet<>());
		Censor.process(event);
		send(PREFIX + "Processed message:" + (event.isCancelled() ? " &c(Cancelled)" : ""));
		send("&eOriginal: &f" + message);
		send("&eResult: &f" + event.getMessage());
	}

	@Path("reload")
	void reload() {
		Censor.reloadConfig();
		send(PREFIX + Censor.getCensorItems().size() + " censor items loaded from disk");
	}

	@Path("debug")
	void debug() {
		send(Censor.getCensorItems().toString());
	}

}
