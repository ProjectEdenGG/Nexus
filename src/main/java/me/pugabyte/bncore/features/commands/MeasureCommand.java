package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MeasureCommand extends CustomCommand {

	public MeasureCommand(CommandEvent event) {
		super(event);
	}

	static Map<UUID, Location> playerMap = new HashMap<>();

	@Path("1")
	void one() {
		playerMap.put(player().getUniqueId(), player().getLocation());
		send(PREFIX + "First position set");
	}

	@Path("2")
	void two() {
		if (!playerMap.containsKey(player().getUniqueId()))
			error("You have not set your first position yet");

		send("&3Distance: &e" + (int) playerMap.get(player().getUniqueId()).distance(player().getLocation()));
		playerMap.remove(player().getUniqueId());
	}

}
