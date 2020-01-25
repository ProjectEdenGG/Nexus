package me.pugabyte.bncore.features.commands.worldedit;

import lombok.SneakyThrows;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGuardUtils;

@DoubleSlash
public class SelCommand extends CustomCommand {

	public SelCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path("[string]")
	void expandAll(@Arg String string) {
		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(player().getWorld());

		switch (string.toLowerCase()) {
			case "tp":
			case "teleport":
				player().teleport(worldGuardUtils.toLocation(worldGuardUtils.getPlayerSelection(player()).getCenter()));
				event.setCancelled(true);
				break;
			case "c":
				runCommand("/sel cuboid");
				event.setCancelled(true);
				break;
			case "p":
				runCommand("/sel poly");
				event.setCancelled(true);
				break;
			case "e":
				runCommand("/sel extend");
				event.setCancelled(true);
				break;
			default:
		}
	}
}

