package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.utils.ColorType;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A perk that gives a user fake armor items specific to their team color. The most basic subclass of this should
 * just override {@link #getColorMaterial(ColorType)} or {@link #getColorItem(ColorType)}. More complex loadouts should
 * override {@link #getColorLoadouts()} and {@link #getMenuItem()}.
 */
public abstract class TeamLoadoutPerk extends LoadoutPerk {
	@Override
	public PerkCategory getPerkCategory() {
		return PerkCategory.TEAM_HAT;
	}

	@Override
	public Map<EnumItemSlot, ItemStack> getLoadout() {
		return getLoadout(ChatColor.DARK_AQUA);
	}

	public Map<ChatColor, Map<EnumItemSlot, ItemStack>> getColorLoadouts() {
		Map<ChatColor, Map<EnumItemSlot, ItemStack>> loadout = new HashMap<>();
		Arrays.stream(ColorType.values()).forEach(colorType -> {
			try {
				if (colorType.getChatColor() != null) {
					loadout.put(colorType.getChatColor(), new HashMap<EnumItemSlot, ItemStack>() {{
						put(EnumItemSlot.HEAD, getColorItem(colorType));
					}});
				}
			} catch (IllegalArgumentException ignored){}
		});
		return loadout;
	}

	public Map<EnumItemSlot, ItemStack> getLoadout(ChatColor chatColor) {
		return getColorLoadouts().getOrDefault(chatColor, new HashMap<>());
	}

	@Override
	public void tick(Minigamer minigamer) {
		if (minigamer.getTeam() == null || minigamer.getMatch().getMechanic().hideTeamLoadoutColors()) {
			tick(minigamer.getPlayer());
			return;
		}
		getLoadout(minigamer.getTeam().getColor()).forEach((itemSlot, itemStack) -> sendColorablePackets(minigamer.getPlayer(), minigamer.getMatch().getPlayers(), itemStack, itemSlot));
	}

	public static ColorType getColorType(ChatColor color) {
		return Arrays.stream(ColorType.values()).filter(colorType -> color.equals(colorType.getChatColor())).findFirst().orElse(null);
	}

	@Override
	public Material getMaterial() {
		return getColorMaterial(ColorType.CYAN);
	}

	protected Material getColorMaterial(ColorType color) {
		return null;
	}

	@Override
	public ItemStack getItem() {
		return getColorItem(ColorType.CYAN);
	}

	protected ItemStack getColorItem(ColorType color) {
		Material material = getColorMaterial(color);
		if (material == null)
			throw new IncompleteTeamLoadout();
		return new ItemStack(material);
	}

	@Override
	protected boolean isColorable(ItemStack item) {
		return true;
	}

	/**
	 * Thrown when a team loadout perk using the default {@link #getColorLoadouts()} has neglected to override {@link #getColorMaterial(ColorType)}
	 */
	public static class IncompleteTeamLoadout extends IncompleteLoadout {}
}
