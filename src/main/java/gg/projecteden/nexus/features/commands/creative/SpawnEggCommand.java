package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

@Permission("essentials.gamemode.creative")
@WikiConfig(rank = "Guest", feature = "Creative")
public class SpawnEggCommand extends CustomCommand {

	public SpawnEggCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Receive a spawn egg for the specified entity type")
	void give(EntityType entityType) {
		try {
			giveItem(new ItemBuilder(Material.AIR).spawnEgg(entityType).build());
		} catch (Exception ex) {
			error("Could not convert entity type " + camelCase(entityType) + " to a spawn egg");
		}
	}

}
