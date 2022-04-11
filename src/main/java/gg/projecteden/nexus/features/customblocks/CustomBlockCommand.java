package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

@Permission(Group.ADMIN)
public class CustomBlockCommand extends CustomCommand {
	private static final CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
	private static CustomBlockTracker tracker;

	public CustomBlockCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			tracker = trackerService.fromWorld(location());
	}

	@Path("list [world]")
	void list(@Arg("current") World world) {
		tracker = trackerService.fromWorld(world);
		Map<Location, CustomBlockData> locationMap = tracker.getLocationMap();
		if (locationMap.isEmpty())
			throw new InvalidInputException("This world has no saved note blocks");

		send("World: " + world.getName());

		for (Location location : locationMap.keySet()) {
			CustomBlockData data = locationMap.get(location);
			send(" " + StringUtils.getCoordinateString(location) + ": " + data.getPlacerUUID() + " Type: " + data.getType());
		}
	}

	@Path("types")
	void list() {
		send("Custom Blocks: ");
		for (CustomBlock customBlock : CustomBlock.values()) {
			send(" - " + customBlock.get().getName());
		}
	}

	@Path("getItem <block>")
	void get(CustomBlock block) {
		giveItem(block.get().getItemStack());
	}
}
