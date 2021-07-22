package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.EntityUtils.getSpawnEgg;

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
