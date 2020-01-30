package me.pugabyte.bncore.features.mcmmo;

import me.pugabyte.bncore.features.mcmmo.menus.McMMOResetMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class McMMOResetCommand extends CustomCommand {
	public McMMOResetCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void McMMOReset() {
		McMMOResetMenu.openMcMMOReset(player());
	}
}
