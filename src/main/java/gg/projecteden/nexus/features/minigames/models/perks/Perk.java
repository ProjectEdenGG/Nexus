package gg.projecteden.nexus.features.minigames.models.perks;

import gg.projecteden.interfaces.Named;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Perk extends IHasPerkCategory, Named {
	@NotNull ItemStack getMenuItem();
	@NotNull PerkCategory getPerkCategory();
	int getPrice();
	@NotNull List<String> getDescription();

	default boolean equals(Perk other) {
		if (this == other)
			return true;
		if (!getName().equals(other.getName()))
			return false;
		return getPerkCategory() == other.getPerkCategory();
	}

	/**
	 * Determines if perk one prevents enabling of perk two
	 */
	static boolean excludes(IHasPerkCategory one, IHasPerkCategory two) {
		return one.excludes(two);
	}
}
