package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Permission(Group.ADMIN)
public class DropAllCommand extends CustomCommand {

	public DropAllCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Drop every available item on the ground")
	void run() {
		for (Material material : Material.values())
			if (material.isItem() && Material.AIR != material)
				world().dropItemNaturally(location(), new ItemStack(material));
	}

}
