package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;

import java.util.UUID;

public class UUIDCommand extends CustomCommand {

	public UUIDCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("View a player's UUID")
	void uuid(@Arg("self") Nerd nerd) {
		send(json("&e" + nerd.getUuid())
				.hover("&3Shift+Click to insert into your chat")
				.insert(nerd.getUuid().toString()));
	}

	@Path("fromString <input>")
	@Description("Create a hashed UUID from your input")
	void fromString(String input) {
		UUID uuid = UUID.nameUUIDFromBytes(input.getBytes());

		send(json("&e" + uuid)
			.hover("&3Shift+Click to insert into your chat")
			.copy(uuid.toString()));
	}

}
