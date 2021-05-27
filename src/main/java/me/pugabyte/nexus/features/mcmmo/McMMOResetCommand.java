package me.pugabyte.nexus.features.mcmmo;

import me.pugabyte.nexus.features.mcmmo.menus.McMMOResetProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGroup;

@Redirect(from = "/mcmmo reset", to = "/mcmmoreset")
public class McMMOResetCommand extends CustomCommand {
	public McMMOResetCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void McMMOReset() {
		if (WorldGroup.of(player()) != WorldGroup.SURVIVAL)
			error("You cannot use this outside of survival");

		new McMMOResetProvider().open(player());
	}
}
