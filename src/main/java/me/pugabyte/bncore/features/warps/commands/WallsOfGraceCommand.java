package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;

public class WallsOfGraceCommand extends CustomCommand {

	public WallsOfGraceCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void wog() {
		Warp wog = new WarpService().get("wallsofgrace", WarpType.NORMAL);
		wog.teleport(player());
		send("&3Warping to the &eWalls of Grace");
	}


}
