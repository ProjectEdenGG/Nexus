package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.operator")
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
