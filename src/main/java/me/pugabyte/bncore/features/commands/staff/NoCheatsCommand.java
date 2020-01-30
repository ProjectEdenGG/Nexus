package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.GameMode;

@Permission("group.staff")
public class NoCheatsCommand extends CustomCommand {

	public NoCheatsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("vanish off");
		runCommand("fly off");
		player().setGameMode(GameMode.SURVIVAL);
		runCommand("god off");
		send("&3Creative, vanish, god, and fly turned off");
	}
}
