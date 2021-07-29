package gg.projecteden.nexus.features.survival.mobequipment;

import gg.projecteden.nexus.features.survival.mobequipment.BiomeModifier.Difficulty;
import gg.projecteden.nexus.features.survival.mobequipment.BiomeModifier.Dimension;
import gg.projecteden.nexus.features.survival.mobequipment.BiomeModifier.Elevation;
import gg.projecteden.nexus.features.survival.mobequipment.BiomeModifier.Moisture;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

public class MobEquipmentCommand extends CustomCommand {

	public MobEquipmentCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("list dimension [dimension]")
	void debugDifficulty(Dimension dimension) {
		for (BiomeModifier biomeModifier : BiomeModifier.valuesBy(Dimension.getModifier())) {
			if (dimension == null || biomeModifier.getDimension().equals(dimension))
				send(" - " + biomeModifier.getName());
		}
	}

	@Path("list difficulty [difficulty]")
	void debugDifficulty(Difficulty difficulty) {
		for (BiomeModifier biomeModifier : BiomeModifier.valuesBy(Difficulty.getModifier())) {
			if (difficulty == null || biomeModifier.getDifficulty().equals(difficulty))
				send(" - " + biomeModifier.getName());
		}
	}

	@Path("list elevation [elevation]")
	void debugDifficulty(Elevation elevation) {
		for (BiomeModifier biomeModifier : BiomeModifier.valuesBy(Elevation.getModifier())) {
			if (elevation == null || biomeModifier.getElevation().equals(elevation))
				send(" - " + biomeModifier.getName());
		}
	}

	@Path("list moisture [moisture]")
	void debugDifficulty(Moisture moisture) {
		for (BiomeModifier biomeModifier : BiomeModifier.valuesBy(Moisture.getModifier())) {
			if (moisture == null || biomeModifier.getMoisture().equals(moisture))
				send(" - " + biomeModifier.getName());
		}
	}
}
