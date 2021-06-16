package me.pugabyte.nexus.features.commands.worldedit;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@DoubleSlash
@Permission("worldedit.region.deform")
public class ScaleCommand extends CustomCommand {

	public ScaleCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<scale>")
	void scale(double scale) {
		runCommand("/deform x/=" + scale + ";y/=" + scale + ";z/=" + scale);
	}

}
