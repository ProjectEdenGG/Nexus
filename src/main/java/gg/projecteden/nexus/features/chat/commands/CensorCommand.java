package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import lombok.NonNull;

import java.util.HashSet;

@Permission(Group.SENIOR_STAFF)
public class CensorCommand extends CustomCommand {

	public CensorCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Description("Test the censor")
	void test(PublicChannel channel, @Vararg String message) {
		ChatEvent event = new PublicChatEvent(new ChatterService().get(player()), channel, message, message, new HashSet<>());
		Censor.process(event);
		send(PREFIX + "Processed message:" + (event.isCancelled() ? " &c(Cancelled)" : ""));
		send("&eOriginal: &f" + event.getOriginalMessage());
		send("&eResult: &f" + event.getMessage());
		send("&eChanged: " + (event.wasChanged() ? "&aYes" : "&cNo"));
	}

	@Description("Reload the censor configuration from disk")
	void reload() {
		Censor.reloadConfig();
		send(PREFIX + Censor.getCensorItems().size() + " censor items loaded from disk");
	}

	@Description("Print the censor configuration in chat")
	void debug() {
		send(Censor.getCensorItems().toString());
	}

}
