package me.pugabyte.nexus.features.commands.staff.admin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.text.DecimalFormat;

@Permission("group.admin")
public class LocationCodeCommand extends CustomCommand {

	public LocationCodeCommand(CommandEvent event) {
		super(event);
	}

	@Path("current")
	void current() {
		send(asJava(player().getLocation()));
	}

	@Path("selection")
	void selection() {
		try {
			WorldEditUtils utils = new WorldEditUtils(player());
			Region selection = utils.getPlayerSelection(player());
			if (selection == null)
				throw new IncompleteRegionException();
			Location loc = utils.toLocation(selection.getMinimumPoint());
			send(asJava(loc));
		} catch (IncompleteRegionException exception) {
			error("Incomplete region. Please select positions 1 & 2");
		}
	}

	public static JsonBuilder asJava(Location loc) {
		DecimalFormat nf = new DecimalFormat("#.00");
		World world = loc.getWorld();
		String worldString = "Bukkit.getWorld(\"" + world.getName() + "\")";
		if (world.equals(Minigames.getWorld())) worldString = "Minigames.getWorld()";

		String locationString = "new Location(" + worldString + ", " +
				nf.format(loc.getX()) + ", " +
				nf.format(loc.getY()) + ", " +
				nf.format(loc.getZ()) + ", " +
				nf.format(loc.getYaw()) + "F, " +
				nf.format(loc.getPitch()) + "F)";

		return new JsonBuilder(locationString).suggest(locationString);
	}

}
