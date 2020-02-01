package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;

@Aliases("lookcenter")
@Permission("group.staff")
public class BlockCenterCommand extends CustomCommand {
	Location centered;

	public BlockCenterCommand(CommandEvent event) {
		super(event);
		centered = Utils.getCenteredLocation(player().getLocation());
	}

	@Path
	void center() {
		player().teleport(centered);
	}

	@Path("yaw")
	void yaw() {
		Location newLocation = player().getLocation().clone();
		newLocation.setYaw(centered.getYaw());
		player().teleport(newLocation);
	}

	@Path("pitch")
	void pitch() {
		Location newLocation = player().getLocation().clone();
		newLocation.setPitch(centered.getPitch());
		player().teleport(newLocation);
	}

	@Path("look")
	void look() {
		Location newLocation = player().getLocation().clone();
		newLocation.setYaw(centered.getYaw());
		newLocation.setPitch(centered.getPitch());
		player().teleport(newLocation);
	}

	@Path("java")
	void java() {
		Location loc = Utils.getCenteredLocation(player().getLocation());
		World world = loc.getWorld();
		String worldString = "Bukkit.getWorld(" + world.getName() + ")";
		if (world.equals(Minigames.getGameworld())) worldString = "Minigames.getGameworld()";

		String locationString = "new Location(" + worldString + ", " + loc.getX() + ", " + loc.getY() + ", " +
				loc.getZ() + ", " + loc.getYaw() + ", " + loc.getPitch() + ")";

		send(json(locationString).suggest(locationString));
	}
}
