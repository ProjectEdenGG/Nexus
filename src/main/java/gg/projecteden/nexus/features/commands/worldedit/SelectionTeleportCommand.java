package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.DoubleSlash;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldEditUtils;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@DoubleSlash
@Permission("worldedit.wand")
@Redirect(from = {"//sel tp", "//sel teleport"}, to = "//selectionteleport")
@Redirect(from = "//sel c", to = "//sel cuboid")
@Redirect(from = "//sel p", to = "//sel poly")
@Redirect(from = "//sel e", to = "//sel extend")
public class SelectionTeleportCommand extends CustomCommand {
	private final WorldEditUtils worldedit;

	public SelectionTeleportCommand(CommandEvent event) {
		super(event);
		worldedit = new WorldEditUtils(player());
	}

	@NoLiterals
	@Description("Teleport to the center of your selection")
	void teleport() {
		Region playerSelection = worldedit.getPlayerSelection(player());
		if (playerSelection == null)
			error("No selection to teleport to");
		player().teleportAsync(worldedit.toLocation(playerSelection.getCenter()), TeleportCause.COMMAND);
	}

}

