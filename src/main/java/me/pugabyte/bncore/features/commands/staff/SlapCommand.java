package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class SlapCommand extends CustomCommand {

	public SlapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void slap(Player player) {
		player.setVelocity(player.getLocation().getDirection().multiply(-2).setY(1));
		send(player, "&6You have been slapped!");
	}

}
