package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.seniorstaff")
public class KillCommand extends CustomCommand {

	public KillCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	public void kill(Player player) {
		player.damage(Short.MAX_VALUE);
		player.setHealth(0);
		send(PREFIX + "Killed " + player.getName());
	}
}
