package gg.projecteden.nexus.features.store.perks.workbenches;

import gg.projecteden.nexus.features.custombenches.DyeStation;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

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
	void open() {
		DyeStation.open(player());
	}

	@Path("cheat")
	@Permission(Group.STAFF)
	void openCheat() {
		DyeStation.openCheat(player());
	}

	@Path("setColor <color>")
	@Permission(Group.STAFF)
	void dye(@Arg(type = ChatColor.class) ChatColor chatColor) {
		ItemStack item = getToolRequired();
		if (!(item.getItemMeta() instanceof LeatherArmorMeta armorMeta))
			return;

		java.awt.Color color = chatColor.getColor();
		armorMeta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
		item.setItemMeta(armorMeta);
	}
}
