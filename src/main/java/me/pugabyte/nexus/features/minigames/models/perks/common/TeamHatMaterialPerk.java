package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface TeamHatMaterialPerk extends TeamHatPerk, HatMaterialPerk {
	@Override
	default Material getMaterial() {
		return getColorMaterial(ColorType.CYAN);
	}

	Material getColorMaterial(ColorType color);

	@Override
	default ItemStack getColorItem(ColorType color) {
		return new ItemStack(getColorMaterial(color));
	}

	// unrelated defaults purgatory
	@Override
	int getPrice();

	@Override
	@NotNull
	String getName();

	@Override
	@NotNull
	String getDescription();

	@Override
	default @NotNull PerkCategory getPerkCategory() {
		return TeamHatPerk.super.getPerkCategory();
	}

	@Override
	default @NotNull ItemStack getItem() {
		return TeamHatPerk.super.getItem();
	}
}
