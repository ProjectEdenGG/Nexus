package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.utils.ColorType;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class TeamLoadoutPerk extends LoadoutPerk {
	@Override
	public Map<EnumItemSlot, ItemStack> getLoadout() {
		return getLoadout(ChatColor.DARK_AQUA);
	}

	public Map<ChatColor, Map<EnumItemSlot, ItemStack>> getColorLoadouts() {
		return basicColorHatMap();
	}

	public Map<EnumItemSlot, ItemStack> getLoadout(ChatColor chatColor) {
		return getColorLoadouts().getOrDefault(chatColor, new HashMap<>());
	}

	@Override
	public void tick(Minigamer minigamer) {
		if (minigamer.getTeam() == null) {
			tick(minigamer.getPlayer());
			return;
		}
		getLoadout(minigamer.getTeam().getColor()).forEach((itemSlot, itemStack) -> sendPackets(minigamer.getPlayer(), minigamer.getMatch().getPlayers(), itemStack, itemSlot));
	}

	public static ColorType getColorType(ChatColor color) {
		return Arrays.stream(ColorType.values()).filter(colorType -> color.equals(colorType.getChatColor())).findFirst().orElse(null);
	}

	protected Material getColorMaterial(ColorType color) {
		return null;
	}

	protected Map<ChatColor, Map<EnumItemSlot, ItemStack>> basicColorHatMap() {
		Map<ChatColor, Map<EnumItemSlot, ItemStack>> loadout = new HashMap<>();
		Arrays.stream(ColorType.values()).forEach(colorType -> {
			try {
				if (colorType.getChatColor() != null) {
					Material material = getColorMaterial(colorType);
					if (material == null)
						throw new IncompleteTeamLoadout();
					loadout.put(colorType.getChatColor(), new HashMap<EnumItemSlot, ItemStack>() {{
						put(EnumItemSlot.HEAD, new ItemStack(material));
					}});
				}
			} catch (IllegalArgumentException ignored){}
			catch (IncompleteTeamLoadout e) {
				e.printStackTrace();
			}
		});
		return loadout;
	}

	/**
	 * Thrown when a team loadout perk using {@link #basicColorHatMap()} has neglected to override {@link #getColorMaterial(ColorType)}
	 */
	protected static class IncompleteTeamLoadout extends Exception {}
}
