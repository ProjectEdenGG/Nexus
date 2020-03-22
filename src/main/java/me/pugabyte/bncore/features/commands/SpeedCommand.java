package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Fallback("essentialsx")
@Permission("essentials.speed")
@Aliases({"flyspeed", "walkspeed"})
public class SpeedCommand extends CustomCommand {

	public SpeedCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void speed() {
		if (player().hasPermission("group.staff")) {
			fallback();
			return;
		}
		int speed;
		try {
			speed = Integer.parseInt(arg(1));
		} catch (NumberFormatException ex) {
			error("You must use a number as the first argument");
			return; //I know it's not needed, but not having caused compiler errors
		}
		if (speed > 3)
			send("&cYou can only use up to speed 3");
		speed = 3;
		args().set(0, String.valueOf(speed));
		fallback();
	}

}
