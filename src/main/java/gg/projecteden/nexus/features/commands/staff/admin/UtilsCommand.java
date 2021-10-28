package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Permission("group.admin")
public class UtilsCommand extends CustomCommand {

	public UtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("setType <material>")
	void setType(Material material) {
		ItemStack tool = getToolRequired();
		tool.setType(material);
	}
}
