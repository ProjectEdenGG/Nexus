package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

@Permission("group.staff")
public class NearestPlayerCommand extends CustomCommand {

	public NearestPlayerCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nearestPlayer() {
		Player nearestPlayer = null;
		double minDistance = Double.MAX_VALUE;
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();

		for (Player player : players) {
			if (player.getWorld() == player().getWorld() && player != player()) {
				double tempDistance = player.getLocation().distance(player().getLocation());
				if (tempDistance < minDistance) {
					nearestPlayer = player;
					minDistance = tempDistance;
				}
			}
		}

		if (nearestPlayer != null)
			send(PREFIX + nearestPlayer.getName() + " is " + (int) minDistance + " blocks away");
		else
			error("No players are nearby");
	}
}
