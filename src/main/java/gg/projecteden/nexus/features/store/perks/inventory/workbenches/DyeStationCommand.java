package gg.projecteden.nexus.features.store.perks.inventory.workbenches;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStationMenu;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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
	@Path("metal <stain>")
	@Permission(Group.STAFF)
	@Description("Plate an item")
	void metal(ColorChoice.MetallicChoice metallicChoice) {
		DecorationUtils.dye(getToolRequired(), metallicChoice, player());
	}
}
