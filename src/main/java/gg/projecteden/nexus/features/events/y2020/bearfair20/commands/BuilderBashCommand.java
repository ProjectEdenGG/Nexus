package gg.projecteden.nexus.features.events.y2020.bearfair20.commands;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Disabled
@HideFromWiki
public class BuilderBashCommand extends CustomCommand {

	public BuilderBashCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("warp builderbash");
	}

}
