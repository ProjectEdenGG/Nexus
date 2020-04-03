package me.pugabyte.bncore.features.commands.creative;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

@Permission("plots.use")
public class SpawnEggCommand extends CustomCommand {

	public SpawnEggCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<entityType>")
	void give(EntityType entityType) {
		if (player().getInventory().getItemInMainHand().getType() != Material.MONSTER_EGG)
			error("You must be holding a spawn egg");

		player().getInventory().setItemInMainHand(new ItemBuilder(Material.MONSTER_EGG).spawnEgg(entityType).build());
	}

}
