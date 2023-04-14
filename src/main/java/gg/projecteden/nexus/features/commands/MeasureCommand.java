package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Distance.distance;

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

		send("&3Distance: &e" + (int) (distance(map.get(uuid()), location().toCenterLocation()).getRealDistance() + 1));
		map.remove(uuid());
	}

}
