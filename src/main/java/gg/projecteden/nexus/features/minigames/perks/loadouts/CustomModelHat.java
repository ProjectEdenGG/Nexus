package gg.projecteden.nexus.features.minigames.perks.loadouts;

import gg.projecteden.nexus.features.minigames.models.perks.common.HatPerk;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomModelHat implements HatPerk {
	private final ItemModelType itemModelType;
	private final String name;
	private final int price;
	private final List<String> description;

	public CustomModelHat(ItemModelType itemModelType, String name, int price, String description) {
		this(itemModelType, name, price, Collections.singletonList(description));
	}

	@Override
	public @NotNull ItemStack getItem() {
		return new ItemBuilder(itemModelType).name(name).build();
	}
}
