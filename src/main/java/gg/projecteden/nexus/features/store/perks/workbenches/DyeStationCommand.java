package gg.projecteden.nexus.features.store.perks.workbenches;

import gg.projecteden.nexus.features.custombenches.DyeStation;
import gg.projecteden.nexus.features.custombenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.interfaces.Colored;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.store.perks.workbenches._WorkbenchCommand.PERMISSION;

@Aliases("dye")
@Permission(PERMISSION)
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
	void run() {
		DyeStation.open(player());
	}

	@Path("cheat")
	@Permission(Group.STAFF)
	void openCheat() {
		DyeStation.openCheat(player());
	}

	@Path("color <color>")
	@Permission(Group.STAFF)
	void dye(ChatColor chatColor) {
		ItemStack item = getToolRequired();
		Colored.of(chatColor.getColor()).apply(item);
	}

	@Path("stain <stain>")
	@Permission(Group.STAFF)
	void dye(StainChoice stainChoice) {
		ItemStack item = getToolRequired();
		Colored.of(stainChoice.getColor()).apply(item);
	}

}
