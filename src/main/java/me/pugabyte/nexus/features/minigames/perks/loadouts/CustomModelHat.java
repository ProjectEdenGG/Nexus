package me.pugabyte.nexus.features.minigames.perks.loadouts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.perks.common.HatPerk;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class CustomModelHat implements HatPerk {
	private final Material material;
	private final int modelID;
	private final String name;
	private final int price;
	private final String description;

	@Override
	public @NotNull ItemStack getItem() {
		// intentionally not using CustomModel#itemOf to avoid NPE
		return new ItemBuilder(material).customModelData(modelID).name(name).build();
	}
}
