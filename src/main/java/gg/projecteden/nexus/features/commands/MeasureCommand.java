package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Distance;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MeasureCommand extends CustomCommand {

	public MeasureCommand(CommandEvent event) {
		super(event);
	}

	private static final Map<UUID, Location> map = new HashMap<>();

	@Path("1")
	@Description("Set your starting point")
	void one() {
		map.put(uuid(), location().toCenterLocation());
		send(PREFIX + "First position set");
	}

	@Path("2")
	@Description("Measure the distance between your starting point and current location")
	void two() {
		if (!map.containsKey(uuid()))
			error("You have not set your first position yet");

		int distance = (int) Distance.distance(map.get(uuid()), location().toCenterLocation()).getRealDistance();
		send("&3Distance: &e" + distance + " (" + (distance + 1) + " blocks)");

		map.remove(uuid());
	}

}
