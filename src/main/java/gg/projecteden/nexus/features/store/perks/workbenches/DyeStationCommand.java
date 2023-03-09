package gg.projecteden.nexus.features.store.perks.workbenches;

import gg.projecteden.nexus.features.workbenches.DyeStation;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
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
	@Description("Open a dye station")
	void run() {
		DyeStation.open(player());
	}

	@Path("cheat")
	@Permission(Group.STAFF)
	@Description("Open a dye station that doesnt require a magic dye")
	void openCheat() {
		DyeStation.openCheat(player());
	}

	@Path("color <color>")
	@Permission(Group.STAFF)
	@Description("Dye an item")
	void dye(ChatColor chatColor) {
		ItemStack item = getToolRequired();
		Colored.of(chatColor.getColor()).apply(item);
		// TODO: APPLY LORE
	}

	@Path("stain <stain>")
	@Description("Stain an item")
	@Permission(Group.STAFF)
	void dye(StainChoice stainChoice) {
		ItemStack item = getToolRequired();
		Colored.of(stainChoice.getColor()).apply(item);
		// TODO: APPLY LORE
	}

	@Path("get magicDye")
	@Permission(Group.ADMIN)
	@Description("Spawn a magic dye item")
	void get_magicDye() {
		giveItem(DyeStation.getMagicDye().build());
	}

	@Path("get magicStain")
	@Permission(Group.ADMIN)
	@Description("Spawn a magic stain item")
	void get_magicStain() {
		giveItem(DyeStation.getMagicStain().build());
	}

	@Path("get paintbrush")
	@Description("Spawn a paintbrush")
	@Permission(Group.ADMIN)
	void get_paintbrush() {
		giveItem(DyeStation.getPaintbrush().build());
	}

}
