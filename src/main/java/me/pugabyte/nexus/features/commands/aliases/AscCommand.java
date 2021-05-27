package me.pugabyte.nexus.features.commands.aliases;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Fallback;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGroup;

@Fallback("askyblock")
public class AscCommand extends CustomCommand {

	public AscCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (WorldGroup.of(world()).equals(WorldGroup.SKYBLOCK)) {
			fallback();
			return;
		}
		runCommand("ascend");
	}

}
