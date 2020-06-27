package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Permission("essentials.top")
public class TopCommand extends CustomCommand {

	public TopCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[y]")
	void run(Integer y) {
		if (y == null)
			y = player().getWorld().getHighestBlockYAt(player().getLocation()) + 1;
		Location top = player().getLocation().clone();
		top.setY(y);
		top.setYaw(player().getLocation().getYaw());
		top.setPitch(player().getLocation().getPitch());
		player().teleport(top, TeleportCause.COMMAND);
		send(PREFIX + "Teleported to top");
	}

}
