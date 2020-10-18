package me.pugabyte.bncore.features.commands.staff.admin;

import com.sk89q.worldedit.IncompleteRegionException;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.World;


@Permission("group.admin")
public class LocationJavaCommand extends CustomCommand {

	public LocationJavaCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void location() {
		try {
			WorldEditUtils utils = new WorldEditUtils(player());
			Location loc = utils.toLocation(utils.getPlayerSelection(player()).getMinimumPoint());

			World world = loc.getWorld();
			String worldString = "Bukkit.getWorld(\"" + world.getName() + "\")";
			if (world.equals(Minigames.getWorld())) worldString = "Minigames.getWorld()";

			String locationString = "new Location(" + worldString + ", " + loc.getX() + ", " + loc.getY() + ", " +
					loc.getZ() + ", " + loc.getYaw() + "F, " + loc.getPitch() + "F)";

			send(json(locationString).suggest(locationString));
		} catch (IncompleteRegionException exception) {
			error("Incomplete region. Please select positions 1 & 2");
		}
	}

}
