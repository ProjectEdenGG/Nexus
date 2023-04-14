package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@WikiConfig(rank = "Guest", feature = "Creative")
public class TopCommand extends CustomCommand {

	public TopCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[y]")
	@Description("Teleport to the highest block at your location or a specific y value")
	void run(Integer y) {
		if (!isStaff() && worldGroup() != WorldGroup.CREATIVE)
			permissionError();

		if (y == null)
			y = world().getHighestBlockYAt(location()) + 1;
		Location top = location().clone();
		top.setY(y);
		top.setYaw(location().getYaw());
		top.setPitch(location().getPitch());
		player().teleportAsync(top, TeleportCause.COMMAND);
	}

}
