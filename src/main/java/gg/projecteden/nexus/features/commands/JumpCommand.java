package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
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

	@NoLiterals
	@Description("Teleport to your target block")
	void run() {
		Location location = getTargetBlockRequired().getLocation().add(0, 1, 0);
		location.setYaw(location().getYaw());
		location.setPitch(location().getPitch());
		player().teleportAsync(location, TeleportCause.COMMAND);
	}

}
