package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * A perk that gives a user fake armor items specific to their team color
 * @see TeamHatPerk
 */
public interface TeamLoadoutPerk extends LoadoutPerk {
	@Override
	default Map<EnumItemSlot, ItemStack> getLoadout() {
		return getLoadout(ChatColor.DARK_AQUA);
	}

	Map<ChatColor, Map<EnumItemSlot, ItemStack>> getColorLoadouts();

	default Map<EnumItemSlot, ItemStack> getLoadout(ChatColor chatColor) {
		return getColorLoadouts().getOrDefault(chatColor, new HashMap<>());
	}

	@Override
	default void tick(Minigamer minigamer) {
		if (minigamer.getTeam() == null || minigamer.getMatch().getMechanic().hideTeamLoadoutColors()) {
			tick(minigamer.getPlayer());
			return;
		}
		getLoadout(minigamer.getTeam().getChatColor()).forEach((itemSlot, itemStack) -> sendColorablePackets(minigamer.getPlayer(), minigamer.getMatch().getPlayers(), itemStack, itemSlot));
	}

}
