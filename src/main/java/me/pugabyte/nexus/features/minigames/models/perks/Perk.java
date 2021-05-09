package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.EqualsAndHashCode;
import me.pugabyte.nexus.framework.interfaces.HasDescription;
import org.bukkit.inventory.ItemStack;

@EqualsAndHashCode
public abstract class Perk implements IHasPerkCategory, HasDescription {
	@EqualsAndHashCode.Include
	public abstract String getName();
	public abstract ItemStack getMenuItem();
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
