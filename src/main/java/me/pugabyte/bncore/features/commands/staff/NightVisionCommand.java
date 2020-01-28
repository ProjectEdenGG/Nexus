package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
@Aliases("nv")
public class NightVisionCommand extends CustomCommand {

	public NightVisionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void on() {
		runCommandAsOp("minecraft:effect " + player().getName() + " minecraft:night_vision 1 1000000 true");
	}

	@Path("off")
	void off() {
		runCommandAsOp("minecraft:effect " + player().getName() + " minecraft:night_vision 1 2 true");
	}
}
