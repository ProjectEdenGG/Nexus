package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.models.noteblock.NoteBlockTracker;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;
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

	@Path("list")
	void list() {
		Map<Location, NoteBlockData> locationMap = tracker.getLocationMap();
		World world = tracker.getWorld();
		if (world == null || locationMap.isEmpty())
			throw new InvalidInputException("This world has no saved note blocks");

		send("World: " + tracker.getWorld().getName());

		for (Location location : locationMap.keySet()) {
			NoteBlockData data = locationMap.get(location);
			send(" " + StringUtils.getCoordinateString(location) + ": " + data.getPlacerUUID());
		}
	}
}
