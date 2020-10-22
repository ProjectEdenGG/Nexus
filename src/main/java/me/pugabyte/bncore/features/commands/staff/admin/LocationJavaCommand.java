package me.pugabyte.bncore.features.commands.staff.admin;

import com.sk89q.worldedit.IncompleteRegionException;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.text.DecimalFormat;

@Permission("group.admin")
public class LocationJavaCommand extends CustomCommand {

	public LocationJavaCommand(CommandEvent event) {
		super(event);
	}

	@Path("current")
	void current() {
		send(javaCode(player().getLocation()));
	}

	@Path("selection")
	void selection() {
		try {
			WorldEditUtils utils = new WorldEditUtils(player());
			Location loc = utils.toLocation(utils.getPlayerSelection(player()).getMinimumPoint());
			send(javaCode(loc));
		} catch (IncompleteRegionException exception) {
			error("Incomplete region. Please select positions 1 & 2");
		}
	}

	public static JsonBuilder javaCode(Location loc) {
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
