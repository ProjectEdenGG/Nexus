package gg.projecteden.nexus.features.commands.worldedit;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@DoubleSlash
@Permission("worldedit.region.deform")
public class ScaleCommand extends CustomCommand {

	public ScaleCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<scale>")
	@Description("Scale your selection")
	void scale(double scale) {
		runCommand("/deform x/=" + scale + ";y/=" + scale + ";z/=" + scale);
	}

}
