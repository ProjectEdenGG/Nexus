package me.pugabyte.bncore.features.commands.worldedit;

import lombok.SneakyThrows;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldEditUtils;

@DoubleSlash
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
			redirectToWorldEdit();
		} else
			switch (string.toLowerCase()) {
				case "tp":
				case "teleport":
					player().teleport(worldEditUtils.toLocation(worldEditUtils.getPlayerSelection(player()).getCenter()));
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
					redirectToWorldEdit();
			}
	}

	private void redirectToWorldEdit() {
		runCommand("worldedit:/sel " + argsString());
	}

}

