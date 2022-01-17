package gg.projecteden.nexus.features.minigames.models.perks.common;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * A perk that gives a user fake armor items specific to their team color
 * @see TeamHatPerk
 */
public interface TeamLoadoutPerk extends LoadoutPerk {
	@Override
	default Map<EquipmentSlot, ItemStack> getLoadout() {
		return getLoadout(ChatColor.DARK_AQUA);
	}

	Map<ChatColor, Map<EquipmentSlot, ItemStack>> getColorLoadouts();

	default Map<EquipmentSlot, ItemStack> getLoadout(ChatColor chatColor) {
		return getColorLoadouts().getOrDefault(chatColor, new HashMap<>());
	}

	@Override
	default void tick(Minigamer minigamer) {
		if (minigamer.getTeam() == null || minigamer.getMatch().getMechanic().hideTeamLoadoutColors()) {
			tick(minigamer.getPlayer());
			return;
		}

		getLoadout(minigamer.getTeam().getChatColor()).forEach((itemSlot, itemStack) ->
			sendColorablePackets(minigamer.getPlayer(), minigamer.getMatch().getPlayers(), itemStack, itemSlot));
	}

}
