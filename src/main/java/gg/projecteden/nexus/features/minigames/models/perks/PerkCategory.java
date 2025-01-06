package gg.projecteden.nexus.features.minigames.models.perks;

import gg.projecteden.nexus.features.resourcepack.decoration.types.Flag.PrideFlagType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public enum PerkCategory implements IHasPerkCategory {
	HAT(2, Material.CREEPER_HEAD),
	TEAM_HAT(2, Material.RED_WOOL),
	PRIDE_FLAG_HAT(2, PrideFlagType.PRIDE.getFlagItem()),
	PARTICLE(1, Material.REDSTONE),
	ARROW_TRAIL(3, Material.SPECTRAL_ARROW),
	GADGET(0, Material.SUGAR),
	;

	/**
	 * Specifies a group of perks of which only one can be enabled, or 0 if the perk can be enabled regardless of the
	 * status of others.<br>
	 * See {@link #excludes(IHasPerkCategory)} to determine if a perk blocks another perk.
	 */
	private final int exclusionGroup;
	private final ItemStack menuItem;

	PerkCategory(int exclusionGroup, Material menuMaterial) {
		this(exclusionGroup, new ItemStack(menuMaterial));
	}

	@Override
	public String toString() {
		return StringUtils.camelCase(name());
	}

	public boolean isExclusive() {
		return exclusionGroup != 0;
	}

	@Override
	public @NotNull PerkCategory getPerkCategory() {
		return this;
	}

	public ItemStack getMenuItem() {
		return new ItemBuilder(menuItem).name("&e"+this).build();
	}
}
