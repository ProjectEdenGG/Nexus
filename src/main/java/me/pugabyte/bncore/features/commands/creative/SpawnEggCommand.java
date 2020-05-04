package me.pugabyte.bncore.features.commands.creative;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.MaterialUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@Permission("plots.use")
public class SpawnEggCommand extends CustomCommand {

	public SpawnEggCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<entityType>")
	void give(EntityType entityType) {
		try {
			player().getInventory().setItemInMainHand(new ItemStack(MaterialUtils.getSpawnEgg(entityType)));
		} catch (Exception ex) {
			error("Could not convert that entity type to a spawn egg");
		}
	}

}
