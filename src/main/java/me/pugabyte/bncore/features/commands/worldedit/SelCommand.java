package me.pugabyte.bncore.features.commands.worldedit;

import com.sk89q.worldedit.regions.Region;
import lombok.SneakyThrows;
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

	public SelCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path("[string]")
	void sel(String string) {
		WorldEditUtils worldEditUtils = new WorldEditUtils(player().getWorld());

		if (string == null) {
			fallback();
		} else
			switch (string.toLowerCase()) {
				case "tp":
				case "teleport":
					Region playerSelection = worldEditUtils.getPlayerSelection(player());
					if (playerSelection == null)
						error("No selection to teleport to");
					player().teleport(worldEditUtils.toLocation(playerSelection.getCenter()), TeleportCause.COMMAND);
					break;
				case "c":
					runCommand("/sel cuboid");
					break;
				case "p":
					runCommand("/sel poly");
					break;
				case "e":
					runCommand("/sel extend");
					break;
				default:
					fallback();
			}
	}

}

