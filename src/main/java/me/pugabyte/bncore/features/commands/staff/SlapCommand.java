package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Permission("group.staff")
public class SlapCommand extends CustomCommand {

	public SlapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void slap(Player player) {
		Vector direction = new Vector(player.getLocation().getX(), 0, player.getLocation().getZ());
		player.setVelocity(direction.multiply(-0.02).add(new Vector(0, 1.5, 0)));
	}


}
