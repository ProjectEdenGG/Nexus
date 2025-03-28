package gg.projecteden.nexus.features.store.perks.inventory.workbenches;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.MineralChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStationMenu;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import net.md_5.bungee.api.ChatColor;

@Aliases("dye")
@Permission(_WorkbenchCommand.PERMISSION)
@WikiConfig(rank = "Store", feature = "Inventory")
public class DyeStationCommand extends _WorkbenchCommand {
	public DyeStationCommand(CommandEvent event) {
		super(event);
	}

	@Override
	protected Workbench getType() {
		return Workbench.DYE_STATION;
	}

	@Path
	@Override
	@Description("Open a dye station")
	void run() {
		DyeStation.open(player());
	}

	@Path("cheat")
	@Permission(Group.STAFF)
	@Description("Open a dye station that doesnt require a magic dye")
	void openCheat() {
		DyeStation.open(player(), DyeStationMenu.DyeStationMode.CHEAT);
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
	void dye(ColorChoice.StainChoice stainChoice) {
		DecorationUtils.dye(getToolRequired(), stainChoice, player());
	}

	@HideFromWiki // Official command is /decoration dye
	@Path("mineral <mineral>")
	@Permission(Group.STAFF)
	@Description("Plate an item with a mineral")
	void metal(MineralChoice metallicChoice) {
		DecorationUtils.dye(getToolRequired(), metallicChoice, player());
	}
}
