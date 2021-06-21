package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.utils.ColorType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A perk that gives a user a fake armor item on their head specific to their team color
 */
public interface TeamHatPerk extends TeamLoadoutPerk, HatPerk {
	@Override
	default @NotNull PerkCategory getPerkCategory() {
		return PerkCategory.TEAM_HAT;
	}

	default Map<ChatColor, Map<EnumWrappers.ItemSlot, ItemStack>> getColorLoadouts() {
		Map<ChatColor, Map<EnumWrappers.ItemSlot, ItemStack>> loadout = new HashMap<>();
		Arrays.stream(ColorType.values()).forEach(colorType -> {
			try {
				loadout.put(colorType.getChatColor(), Map.of(
					EnumWrappers.ItemSlot.HEAD, getColorItem(colorType)
				));
			} catch (IllegalArgumentException ignored){}
		});
		return loadout;
	}

	@Override
	default @NotNull ItemStack getItem() {
		return getColorItem(ColorType.CYAN);
	}

	ItemStack getColorItem(ColorType color);

	@Override
	default boolean isColorable(ItemStack item) {
		return true;
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
	default @NotNull ItemStack getMenuItem() {
		return HatPerk.super.getMenuItem();
	}

	@Override
	default @NotNull Map<EnumWrappers.ItemSlot, ItemStack> getLoadout() {
		return TeamLoadoutPerk.super.getLoadout();
	}
}
