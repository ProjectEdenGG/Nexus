package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

// What the fuck essentials
@Aliases({"eclearinventorytoggle", "clearinventoryconfirmtoggle", "eclearinventoryconfirmtoggle",
		"clearinventoryconfirmoff", "eclearinventoryconfirmoff", "clearconfirmoff", "eclearconfirmoff",
		"clearconfirmon", "eclearconfirmon", "clearconfirm", "eclearconfirm"})
public class ClearInventoryToggleCommand extends CustomCommand {

	ClearInventoryToggleCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void info() {
		send(PREFIX + "Use &c/clear undo &3to revert an inventory clear");
	}

}
