package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.EqualsAndHashCode;
import org.bukkit.inventory.ItemStack;

@EqualsAndHashCode
public abstract class Perk implements IHasPerkCategory {
	@EqualsAndHashCode.Include
	public abstract String getName();
	public abstract ItemStack getMenuItem();
	public abstract String getDescription();
	@EqualsAndHashCode.Include
	public abstract PerkCategory getPerkCategory();
	public abstract int getPrice();

	/**
	 * Determines if perk one prevents enabling of perk two
	 */
	public static boolean excludes(IHasPerkCategory one, IHasPerkCategory two) {
		return one.excludes(two);
	}
}
