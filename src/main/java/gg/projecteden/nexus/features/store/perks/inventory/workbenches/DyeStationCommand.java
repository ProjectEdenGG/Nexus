package gg.projecteden.nexus.features.store.perks.inventory.workbenches;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.workbenches.DyeStation;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMode;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import net.md_5.bungee.api.ChatColor;

import static gg.projecteden.nexus.features.store.perks.inventory.workbenches._WorkbenchCommand.PERMISSION;

@Aliases("dye")
@Permission(PERMISSION)
@WikiConfig(rank = "Store", feature = "Inventory")
public class DyeStationCommand extends _WorkbenchCommand {
	public DyeStationCommand(CommandEvent event) {
		super(event);
	}

	@Override
	protected Workbench getType() {
		return Workbench.DYE_STATION;
	}

	@NoLiterals
	@Override
	@Description("Open a dye station")
	void run() {
		DyeStation.open(player());
	}

	@Path("cheat")
	@Permission(Group.STAFF)
	@Description("Open a dye station that doesnt require a magic dye")
	void openCheat() {
		DyeStation.open(player(), DyeStationMode.CHEAT);
	}

	@HideFromWiki // Official command is /decoration dye
	@Path("color <color>")
	@Permission(Group.STAFF)
	@Description("Dye an item")
	void dye(ChatColor chatColor) {
		DecorationUtils.dye(getToolRequired(), chatColor, player());
	}

	@HideFromWiki // Official command is /decoration dye
	@Path("stain <stain>")
	@Permission(Group.STAFF)
	@Description("Stain an item")
	void dye(StainChoice stainChoice) {
		DecorationUtils.dye(getToolRequired(), stainChoice, player());
	}
}
