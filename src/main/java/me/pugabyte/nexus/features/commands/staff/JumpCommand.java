package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Aliases("j")
@Permission("worldedit.navigation.jumpto")
public class JumpCommand extends CustomCommand {

	public JumpCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		Location location = getTargetBlockRequired().getLocation().add(0, 1, 0);
		location.setYaw(location().getYaw());
		location.setPitch(location().getPitch());
		player().teleport(location, TeleportCause.COMMAND);
	}

}
