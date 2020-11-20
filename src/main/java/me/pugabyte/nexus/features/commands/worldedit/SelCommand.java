package me.pugabyte.nexus.features.commands.worldedit;

import com.sk89q.worldedit.regions.Region;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.nexus.framework.commands.models.annotations.Fallback;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldEditUtils;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@DoubleSlash
@Fallback("worldedit")
@Permission("worldedit.wand")
public class SelCommand extends CustomCommand {
	private final WorldEditUtils worldEditUtils;

	public SelCommand(CommandEvent event) {
		super(event);
		worldEditUtils = new WorldEditUtils(player());
	}

	@Path
	void run() {
		runCommand("/desel");
	}

	@Path("(tp|teleport)")
	void teleport(String string) {
		Region playerSelection = worldEditUtils.getPlayerSelection(player());
		if (playerSelection == null)
			error("No selection to teleport to");
		player().teleport(worldEditUtils.toLocation(playerSelection.getCenter()), TeleportCause.COMMAND);
	}

	@Path("c")
	void cuboid() {
		runCommand("/sel cuboid");
	}

	@Path("p")
	void poly() {
		runCommand("/sel poly");
	}

	@Path("e")
	void extend() {
		runCommand("/sel extend");
	}

}

