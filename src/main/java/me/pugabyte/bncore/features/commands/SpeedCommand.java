package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Fallback("essentials")
@Permission("essentials.speed")
@Aliases({"flyspeed", "walkspeed", "espeed"})
public class SpeedCommand extends CustomCommand {

	public SpeedCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void speed() {
		if (isConsole()) {
			fallback();
			return;
		}

		if (player().hasPermission("group.staff")) {
			fallback();
			return;
		}

		if (!isIntArg(1))
			error("First argument must be a number");

		int speed = intArg(1);
		if (speed > 3) {
			send("&cYou can only use up to speed 3");
			speed = 3;
		}

		args().set(0, String.valueOf(speed));
		fallback();
	}

}
