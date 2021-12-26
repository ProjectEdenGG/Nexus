package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Permission(Group.ADMIN)
public class ItemUtilsCommand extends CustomCommand {

	public ItemUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("setType <material>")
	void setType(Material material) {
		ItemStack tool = getToolRequired();
		tool.setType(material);
	}
}
