package gg.projecteden.nexus.features.commands.worldedit;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.DoubleSlash;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;

@DoubleSlash
@Permission("worldedit.region.deform")
public class ScaleCommand extends CustomCommand {

	public ScaleCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Scale your selection")
	void scale(double scale) {
		runCommand("/deform x/=" + scale + ";y/=" + scale + ";z/=" + scale);
	}

}
