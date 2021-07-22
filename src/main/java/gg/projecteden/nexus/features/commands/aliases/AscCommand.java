package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Fallback;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldGroup;

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
