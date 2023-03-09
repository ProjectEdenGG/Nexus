package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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

	@Path
	@Description("Teleport to the center of your selection")
	void teleport() {
		Region playerSelection = worldedit.getPlayerSelection(player());
		if (playerSelection == null)
			error("No selection to teleport to");
		player().teleportAsync(worldedit.toLocation(playerSelection.getCenter()), TeleportCause.COMMAND);
	}

}

