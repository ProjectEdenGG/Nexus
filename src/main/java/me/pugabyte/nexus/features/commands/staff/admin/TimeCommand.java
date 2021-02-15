package me.pugabyte.nexus.features.commands.staff.admin;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.utils.DescParseTickFormat;
import org.bukkit.World;

@Permission("group.admin")
public class TimeCommand extends CustomCommand {

	public TimeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void tellTime() {
		World world = player().getWorld();
		long ticks = world.getTime();
		send(PREFIX + "The world time for &e" + world.getName() + " &3is &e" + DescParseTickFormat.format24(ticks) +
				" &3or &e" + DescParseTickFormat.format12(ticks) + " &3 or &e" + ticks + " ticks");
	}

	@Path("set <time> [world]")
	void setTime(String time, @Arg("current") World world) {
		long ticks;
		try {
			ticks = DescParseTickFormat.parse(time);
		} catch (Exception ex) {
			throw new InvalidInputException("Unable to process time " + time);
		}
		world.setTime(ticks);
		send(PREFIX + "Set the world time for world &e" + world.getName() + " &3is &e" + DescParseTickFormat.format24(ticks) +
				" &3or &e" + DescParseTickFormat.format12(ticks) + " &3 or &e" + ticks + " ticks");
	}

}
