package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.survival.Sleep;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.DescParseTickFormat;
import org.bukkit.World;

@Permission(Group.ADMIN)
public class TimeCommand extends CustomCommand {

	public TimeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void tellTime() {
		World world = world();
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
		Sleep.WorldTimeSync.syncWorlds(Sleep.SleepWorlds.of(world.getName()), world);
	}

}
