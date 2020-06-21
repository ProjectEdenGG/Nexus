package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Permission("group.seniorstaff")
public class DropAllCommand extends CustomCommand {

	public DropAllCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		for (Material material : Material.values()) {
			if (material.isItem() && Material.AIR != material)
				player().getWorld().dropItemNaturally(player().getLocation(), new ItemStack(material));
		}
	}

}
