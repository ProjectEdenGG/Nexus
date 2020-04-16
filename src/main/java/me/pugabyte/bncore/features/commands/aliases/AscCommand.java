package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGroup;

@Fallback("askyblock")
public class AscCommand extends CustomCommand {

	public AscCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (WorldGroup.get(player().getWorld()).equals(WorldGroup.SKYBLOCK)) fallback();
		runCommand("ascend");
	}

}
