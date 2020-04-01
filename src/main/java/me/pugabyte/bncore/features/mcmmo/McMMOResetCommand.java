package me.pugabyte.bncore.features.mcmmo;

import me.pugabyte.bncore.features.mcmmo.menus.McMMOResetMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGroup;

@Redirect(from = "/mcmmo reset", to = "/mcmmoreset")
public class McMMOResetCommand extends CustomCommand {
	public McMMOResetCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void McMMOReset() {
		if (WorldGroup.get(player()) != WorldGroup.SURVIVAL)
			error("You cannot use this outside of survival");

		McMMOResetMenu.openMcMMOReset(player());
	}
}
