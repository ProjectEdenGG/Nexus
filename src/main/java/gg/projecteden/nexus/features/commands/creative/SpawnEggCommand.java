package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

@Permission("essentials.gamemode.creative")
public class SpawnEggCommand extends CustomCommand {

	public SpawnEggCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<entityType>")
	@Description("Converts an entity into a spawn egg when looking at it")
	void give(EntityType entityType) {
		try {
			giveItem(new ItemBuilder(Material.AIR).spawnEgg(entityType).build());
		} catch (Exception ex) {
			error("Could not convert entity type " + camelCase(entityType) + " to a spawn egg");
		}
	}

}
