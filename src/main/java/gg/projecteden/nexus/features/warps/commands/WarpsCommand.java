package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.features.warps.WarpMenu;
import gg.projecteden.nexus.features.warps.WarpsMenu;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Redirect(from = "/survival", to = "/warp survival")
@Redirect(from = "/plaza", to = "/warp plaza")
@Redirect(from = "/mall", to = "/warp mall")
@Redirect(from = {"/shub", "/shophub"}, to = "/warp shub")
@Redirect(from = "/creative", to = "/warp creative")
@Redirect(from = "/skyblock", to = "/warp skyblock")
@Redirect(from = {"/minigames", "/gamelobby", "/gl"}, to = "/warp minigames")
@Aliases({"warp", "go", "goto", "hub", "tphub", "server", "servers", "lobby"})
public class WarpsCommand extends _WarpCommand {

	public WarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.NORMAL;
	}

	@Path
	void menu() {
		WarpsMenu.open(player(), WarpMenu.MAIN);
	}

	@Path("types")
	@Permission("group.staff")
	void types() {
		send(PREFIX + "Valid warp types:");
		for (WarpType type : WarpType.values())
			send(" " + camelCase(type));
	}

}
