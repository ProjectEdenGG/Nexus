package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.features.mcmmo.menus.McMMOResetProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldGroup;

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
