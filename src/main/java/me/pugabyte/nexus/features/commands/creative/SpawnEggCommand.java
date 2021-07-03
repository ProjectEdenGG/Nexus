package me.pugabyte.nexus.features.commands.creative;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.EntityUtils.getSpawnEgg;

@Permission("essentials.gamemode.creative")
public class SpawnEggCommand extends CustomCommand {

	public SpawnEggCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<entityType>")
	void give(EntityType entityType) {
		try {
			inventory().setItemInMainHand(new ItemStack(getSpawnEgg(entityType)));
		} catch (Exception ex) {
			error("Could not convert that entity type to a spawn egg");
		}
	}

}
