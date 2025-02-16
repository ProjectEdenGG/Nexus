package gg.projecteden.nexus.features.minigames.perks.loadouts.teamed;

import gg.projecteden.nexus.features.minigames.models.perks.common.TeamHatPerk;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class DyeableCustomModelHat implements TeamHatPerk {
	private final @NotNull String name;
	private final int price;
	private final @NotNull List<String> description;
	private final @NotNull Material material;
	private final String modelId;

	public DyeableCustomModelHat(@NotNull String name, int price, @NotNull String description, @NotNull Material material, String modelId) {
		this(name, price, Collections.singletonList(description), material, modelId);
	}

	@Override
	public @NotNull ItemStack getColorItem(ColorType color) {
		return new ItemBuilder(material).model(modelId).dyeColor(color).name(name).build();
	}

	public static @NotNull DyeableCustomModelHat createPirateHat(@NotNull String name, String modelId) {
		return new DyeableCustomModelHat(name, 100, "Show off your love for the seven seas with this pirate hat", Material.LEATHER_HORSE_ARMOR, modelId);
	}
}
