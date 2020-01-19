package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;

@Permission("bncore.teleport")
public class BlockCenterCommand extends CustomCommand {

	public BlockCenterCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void blockCenter() {
		player().teleport(Utils.getCenteredLocation(player().getLocation()));
	}

	@Path("java")
	void java() {
		Location loc = Utils.getCenteredLocation(player().getLocation());
		World world = loc.getWorld();
		String worldString = "Bukkit.getWorld(" + world.getName() + ")";
		if (world.equals(Minigames.getGameworld())) worldString = "Minigames.getGameworld()";

		String locationString = "new Location(" + worldString + ", " + loc.getX() + ", " + loc.getY() + ", " +
				loc.getZ() + ", " + loc.getYaw() + ", " + loc.getPitch() + ")";

		SkriptFunctions.json(player(), locationString + "||sgt:" + locationString);
	}
}
