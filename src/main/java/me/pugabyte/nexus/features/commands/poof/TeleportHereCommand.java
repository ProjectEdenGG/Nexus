package me.pugabyte.nexus.features.commands.poof;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Aliases({"tphere", "s"})
public class TeleportHereCommand extends CustomCommand {

	public TeleportHereCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Player player) {
		if (!player().hasPermission("essentials.tphere"))
			runCommand("tpahere " + argsString());
		else
			player.teleportAsync(player().getLocation(), TeleportCause.COMMAND);
	}

}
