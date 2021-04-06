package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate;

import me.pugabyte.nexus.features.minigames.models.perks.common.TeamLoadoutPerk;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class BasePirateHat extends TeamLoadoutPerk {
	protected static int colorOffset(ColorType color) {
		// unused: leather (5) and mint (11)
		switch (color) {
			case WHITE:
				return 0;
			case LIGHT_GRAY:
				return 1;
			case GRAY:
				return 2;
			case BLACK:
				return 3;
			case BROWN:
				return 4;
			case RED:
			case LIGHT_RED:
				return 6;
			case ORANGE:
				return 7;
			case YELLOW:
				return 8;
			case GREEN:
				return 9;
			case LIGHT_GREEN:
				return 10;
			case CYAN:
				return 12;
			case LIGHT_BLUE:
				return 13;
			case BLUE:
				return 14;
			case PURPLE:
				return 15;
			case MAGENTA:
				return 16;
			case PINK:
				return 17;
			default:
				throw new IllegalStateException("Unexpected value: " + color);
		}
	}

	/**
	 * Gets the pirate hat for a team
	 * @param offset integer offset for a type of pirate hat
	 * @param color team color to use
	 * @return item stack of the pirate hat
	 */
	protected ItemStack getPirateHat(int offset, ColorType color) {
		return new ItemBuilder(Material.STONE_BUTTON).customModelData(offset + colorOffset(color)*4).name("&f"+getName()).build();
	}

	@Override
	protected abstract ItemStack getColorItem(ColorType color);

	@Override
	public ItemStack getItem() {
		return getColorItem(ColorType.BLACK);
	}

	@Override
	public Map<EnumItemSlot, ItemStack> getLoadout() {
		return getLoadout(ChatColor.BLACK);
	}

	@Override
	public String getDescription() {
		return "Show off your love for the seven seas with this pirate hat";
	}

	@Override
	public int getPrice() {
		return 100;
	}

	@Override
	protected boolean isColorable(ItemStack item) {
		return true;
	}
}
