package gg.projecteden.nexus.features.customblocks.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.models.customblock.NoteBlockTracker;
import gg.projecteden.nexus.models.customblock.NoteBlockTrackerService;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

@Permission(Group.ADMIN)
public class NoteBlockCommand extends CustomCommand {
	private static final NoteBlockTrackerService trackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker tracker;

	public NoteBlockCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			tracker = trackerService.fromWorld(location());
	}

	@Path("list [world]")
	void list(@Arg("current") World world) {
		tracker = trackerService.fromWorld(world);
		Map<Location, NoteBlockData> locationMap = tracker.getLocationMap();
		if (locationMap.isEmpty())
			throw new InvalidInputException("This world has no saved note blocks");

		send("World: " + world.getName());

		for (Location location : locationMap.keySet()) {
			NoteBlockData data = locationMap.get(location);
			send(" " + StringUtils.getCoordinateString(location) + ": " + data.getPlacerUUID());
		}
	}
}
