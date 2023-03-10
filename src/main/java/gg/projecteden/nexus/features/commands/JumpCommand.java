package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Aliases("j")
@Permission("worldedit.navigation.jumpto")
@WikiConfig(rank = "Guest", feature = "Creative")
public class JumpCommand extends CustomCommand {

	public JumpCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Teleport to your target block")
	void run() {
		Location location = getTargetBlockRequired().getLocation().add(0, 1, 0);
		location.setYaw(location().getYaw());
		location.setPitch(location().getPitch());
		player().teleportAsync(location, TeleportCause.COMMAND);
	}

}
