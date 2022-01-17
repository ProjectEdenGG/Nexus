package gg.projecteden.nexus.features.minigames.perks.loadouts.teamed.pirate;

import gg.projecteden.nexus.features.minigames.models.perks.common.TeamHatPerk;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IPirateHat extends TeamHatPerk {
	static int colorOffset(ColorType color) {
		// unused: leather (5) and mint (11)
		return switch (color) {
			case WHITE -> 0;
			case LIGHT_GRAY -> 1;
			case GRAY -> 2;
			case BLACK -> 3;
			case BROWN -> 4;
			case RED, LIGHT_RED -> 6;
			case ORANGE -> 7;
			case YELLOW -> 8;
			case GREEN -> 9;
			case LIGHT_GREEN -> 10;
			case CYAN -> 12;
			case LIGHT_BLUE -> 13;
			case BLUE -> 14;
			case PURPLE -> 15;
			case MAGENTA -> 16;
			case PINK -> 17;
		};
	}

	/**
	 * Gets the pirate hat for a team
	 * @param offset integer offset for a type of pirate hat
	 * @param color team color to use
	 * @return item stack of the pirate hat
	 */
	default ItemStack getPirateHat(int offset, ColorType color) {
		return new ItemBuilder(Material.STONE_BUTTON).customModelData(offset + colorOffset(color)*4).name("&f"+getName()).build();
	}

	@Override
	default @NotNull ItemStack getItem() {
		return getColorItem(ColorType.BLACK);
	}

	@Override
	default @NotNull Map<EquipmentSlot, ItemStack> getLoadout() {
		return getLoadout(ChatColor.BLACK);
	}

	@Override
	default @NotNull String getDescription() {
		return "Show off your love for the seven seas with this pirate hat";
	}

	@Override
	default int getPrice() {
		return 100;
	}
}
