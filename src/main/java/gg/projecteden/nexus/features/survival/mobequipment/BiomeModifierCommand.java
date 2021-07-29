package gg.projecteden.nexus.features.survival.mobequipment;

import gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeModifierType;
import gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.IBiomeModifier;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.utils.EnumUtils;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public class BiomeModifierCommand extends CustomCommand {

	public BiomeModifierCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("list <modifier> <value>")
	void list(BiomeModifierType modifierType, @Arg(context = 1) IBiomeModifier modifier) {
		final List<BiomeModifierConfig> biomes = BiomeModifierConfig.valuesBy(modifier);
		if (biomes.isEmpty())
			error("No matching biomes found");

		send(PREFIX + "Biomes with &e" + camelCase(modifierType) + " &3modifier &e" + camelCase(modifier.name()));
		for (BiomeModifierConfig biome : biomes)
			send(" &7- &e" + biome.getName());
	}

	@ConverterFor(IBiomeModifier.class)
	IBiomeModifier convertToIBiomeModifier(String value, BiomeModifierType context) {
		try {
			return EnumUtils.valueOf(context.getModifierClass(), value);
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException(camelCase(context) + " modifier &e" + value + " &cnot found");
		}
	}

	@TabCompleterFor(IBiomeModifier.class)
	List<String> tabCompleteIBiomeModifier(String filter, BiomeModifierType context) {
		return Arrays.stream(context.getModifierClass().getEnumConstants())
			.map(constant -> constant.name().toLowerCase())
			.filter(name -> name.startsWith(filter.toLowerCase()))
			.toList();
	}

}
