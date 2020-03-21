package me.pugabyte.bncore.features.commands.worldedit;

import com.sk89q.worldedit.regions.Region;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@DoubleSlash
@Fallback("worldedit")
@Permission("worldedit.wand")
public class SelCommand extends CustomCommand {
	WorldEditUtils worldEditUtils;

	public SelCommand(CommandEvent event) {
		super(event);
		worldEditUtils = new WorldEditUtils(player().getWorld());
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

