package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;

import java.util.UUID;

public class UUIDCommand extends CustomCommand {

	public UUIDCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[player]")
	@Description("View a player's UUID")
	void uuid(@Optional("self") Nerd nerd) {
		send(json("&e" + nerd.getUuid())
				.hover("&3Shift+Click to insert into your chat")
				.insert(nerd.getUuid().toString()));
	}

	@Path("fromString <input>")
	@Permission(Group.ADMIN)
	@Description("Create a hashed UUID from your input")
	void fromString(String input) {
		UUID uuid = UUID.nameUUIDFromBytes(input.getBytes());

		send(json("&e" + uuid)
			.hover("&3Shift+Click to insert into your chat")
			.copy(uuid.toString()));
	}

}
