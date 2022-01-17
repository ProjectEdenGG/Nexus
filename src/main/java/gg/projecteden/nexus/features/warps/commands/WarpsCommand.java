package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.features.warps.WarpMenu;
import gg.projecteden.nexus.features.warps.Warps.LegacySurvivalWarp;
import gg.projecteden.nexus.features.warps.Warps.SurvivalWarp;
import gg.projecteden.nexus.features.warps.WarpsMenu;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Redirect(from = "/skyblock", to = "/warp skyblock")
@Redirect(from = "/is", to = "/ob")
@Aliases({"warp", "go", "goto", "server", "servers"})
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
	@Permission(Group.ADMIN)
	void types() {
		send(PREFIX + "Valid warp types:");
		for (WarpType type : WarpType.values())
			send(" " + camelCase(type));
	}

	@Path("updateFlags")
	@Permission(Group.ADMIN)
	void updateWarpFlags() {
		for (SurvivalWarp warp : SurvivalWarp.values()) {
			if (warp == SurvivalWarp.SPAWN) continue;
			runCommand("rg flag -w \"survival\" warp_" + warp.name().toLowerCase() + " greeting");
			runCommand("rg flag -w \"survival\" warp_" + warp.name().toLowerCase() + " farewell");
			runCommand("rg flag -w \"survival\" warp_" + warp.name().toLowerCase() + " nexus-title-fade 10");
			runCommand("rg flag -w \"survival\" warp_" + warp.name().toLowerCase() + " nexus-title-ticks 30");
			runCommand("rg flag -w \"survival\" warp_" + warp.name().toLowerCase() + " nexus-actionbar-ticks 80");
			runCommand("rg flag -w \"survival\" warp_" + warp.name().toLowerCase() + " nexus-farewell-actionbar &4&lPlease move 100+ blocks away");
			runCommand("rg flag -w \"survival\" warp_" + warp.name().toLowerCase() + " nexus-farewell-subtitle &eExiting &3the &6" + camelCase(warp).replace(" ", " #") + " Warp");
			runCommand("rg flag -w \"survival\" warp_" + warp.name().toLowerCase() + " nexus-greeting-subtitle &eEntering &3the &6" + camelCase(warp).replace(" ", " #") + " Warp");
		}

		runCommand("rg flag -w \"survival\" spawn greeting");
		runCommand("rg flag -w \"survival\" spawn farewell");
		runCommand("rg flag -w \"survival\" spawn nexus-title-fade 10");
		runCommand("rg flag -w \"survival\" spawn nexus-title-ticks 30");
		runCommand("rg flag -w \"survival\" spawn nexus-actionbar-ticks 80");
		runCommand("rg flag -w \"survival\" spawn nexus-farewell-actionbar &4&lPlease move 100+ blocks away");
		runCommand("rg flag -w \"survival\" spawn nexus-farewell-subtitle &eExiting &6Spawn");
		runCommand("rg flag -w \"survival\" spawn nexus-greeting-subtitle &eEntering &6Spawn");

		for (LegacySurvivalWarp warp : LegacySurvivalWarp.values()) {
			if (warp == LegacySurvivalWarp.NETHER || warp == LegacySurvivalWarp.SPAWN) continue;
			runCommand("rg flag -w \"world\" warp_" + warp.name().toLowerCase().replace("_", "") + " greeting");
			runCommand("rg flag -w \"world\" warp_" + warp.name().toLowerCase().replace("_", "") + " farewell");
			runCommand("rg flag -w \"world\" warp_" + warp.name().toLowerCase().replace("_", "") + " nexus-title-fade 10");
			runCommand("rg flag -w \"world\" warp_" + warp.name().toLowerCase().replace("_", "") + " nexus-title-ticks 30");
			runCommand("rg flag -w \"world\" warp_" + warp.name().toLowerCase().replace("_", "") + " nexus-actionbar-ticks 80");
			runCommand("rg flag -w \"world\" warp_" + warp.name().toLowerCase().replace("_", "") + " nexus-farewell-actionbar &4&lPlease move 100+ blocks away");
			runCommand("rg flag -w \"world\" warp_" + warp.name().toLowerCase().replace("_", "") + " nexus-farewell-subtitle &eExiting &3the &6" + camelCase(warp).replace(" ", " #") + " Warp");
			runCommand("rg flag -w \"world\" warp_" + warp.name().toLowerCase().replace("_", "") + " nexus-greeting-subtitle &eEntering &3the &6" + camelCase(warp).replace(" ", " #") + " Warp");
		}

		runCommand("rg flag -w \"world\" spawn greeting");
		runCommand("rg flag -w \"world\" spawn farewell");
		runCommand("rg flag -w \"world\" spawn nexus-title-fade 10");
		runCommand("rg flag -w \"world\" spawn nexus-title-ticks 30");
		runCommand("rg flag -w \"world\" spawn nexus-actionbar-ticks 80");
		runCommand("rg flag -w \"world\" spawn nexus-farewell-actionbar &4&lPlease move 100+ blocks away");
		runCommand("rg flag -w \"world\" spawn nexus-farewell-subtitle &eExiting &6Spawn");
		runCommand("rg flag -w \"world\" spawn nexus-greeting-subtitle &eEntering &6Spawn");
	}

}
