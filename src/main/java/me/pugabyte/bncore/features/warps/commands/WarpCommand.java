package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.features.warps.WarpMenu;
import me.pugabyte.bncore.features.warps.WarpsMenu;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.WarpType;

@Redirect(from = "/survival", to = "/warp survival")
@Redirect(from = "/market", to = "/warp market")
@Redirect(from = "/plaza", to = "/warp plaza")
@Redirect(from = "/mall", to = "/warp mall")
@Redirect(from = {"/shub", "/shophub"}, to = "/warp shub")
@Redirect(from = "/creative", to = "/warp creative")
@Redirect(from = "/skyblock", to = "/warp skyblock")
@Redirect(from = {"/minigames", "/gamelobby", "/gl"}, to = "/warp minigames")
@Aliases("warps")
public class WarpCommand extends _WarpCommand {

	public WarpCommand(CommandEvent event) {
		super(event);
	}

	@Override
	WarpType getWarpType() {
		return WarpType.NORMAL;
	}

	@Path
	void menu() {
		WarpsMenu.open(player(), WarpMenu.MAIN);
	}

}
