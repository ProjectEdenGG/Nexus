package gg.projecteden.nexus.features.minigames.perks.loadouts;

import gg.projecteden.nexus.features.minigames.models.perks.common.HatPerk;
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
public class CustomModelHat implements HatPerk {
	private final Material material;
	private final int modelID;
	private final String name;
	private final int price;
	private final List<String> description;

	public CustomModelHat(Material material, int modelId, String name, int price, String description) {
		this(material, modelId, name, price, Collections.singletonList(description));
	}

	@Override
	public @NotNull ItemStack getItem() {
		// intentionally not using CustomModel#itemOf to avoid NPE
		return new ItemBuilder(material).customModelData(modelID).name(name).build();
	}
}
