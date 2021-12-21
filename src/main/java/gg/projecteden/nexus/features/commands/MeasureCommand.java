package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Description("Measure the distance from one point to another")
public class MeasureCommand extends CustomCommand {

	public MeasureCommand(CommandEvent event) {
		super(event);
	}

	private static final Map<UUID, Location> map = new HashMap<>();

	@Path("1")
	void one() {
		map.put(uuid(), location().toCenterLocation());
		send(PREFIX + "First position set");
	}

	@Path("2")
	void two() {
		if (!map.containsKey(uuid()))
			error("You have not set your first position yet");

		send("&3Distance: &e" + (int) (map.get(uuid()).distance(location().toCenterLocation()) + 1));
		map.remove(uuid());
	}

}
