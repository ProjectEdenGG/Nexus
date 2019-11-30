package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

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
		reply(PREFIX + "Use &c/clear undo &3to revert an inventory clear");
	}

}
