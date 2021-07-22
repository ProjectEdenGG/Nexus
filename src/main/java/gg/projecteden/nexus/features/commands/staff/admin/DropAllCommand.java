package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Permission("group.admin")
public class DropAllCommand extends CustomCommand {

	public DropAllCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		for (Material material : Material.values())
			if (material.isItem() && Material.AIR != material)
				world().dropItemNaturally(location(), new ItemStack(material));
	}

}
