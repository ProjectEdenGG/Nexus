package gg.projecteden.nexus.features.events.y2020.bearfair20.commands;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;

@Disabled
@HideFromWiki
public class BuilderBashCommand extends CustomCommand {

	public BuilderBashCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	void run() {
		runCommand("warp builderbash");
	}

}
