package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

public class DeopCommand extends CustomCommand {

	public DeopCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Permission("group.admin")
	public void deop(Player player) {
		String name = player.getName();
		if (!player.isOp())
			error(name + " is not a server operator");

		player.setOp(false);
		send(PREFIX + name + " is no longer a server operator");

		if (!player.equals(player()))
			send(player, PREFIX + "You are no longer a server operator");
	}
}
