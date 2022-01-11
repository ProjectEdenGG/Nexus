package gg.projecteden.nexus.features.store.perks.stattrack.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Data
public class StatIncreaseEvent {
	private static final List<WorldGroup> ENABLED_WORLDS = List.of(WorldGroup.SURVIVAL, WorldGroup.ONEBLOCK, WorldGroup.SKYBLOCK);

	public StatIncreaseEvent(Player player, ItemStack item, Stat stat, int value) {
		if (!Dev.GRIFFIN.is(player))
			return;

		Nexus.log("StatIncreaseEvent{player=" + player.getName() + ", item=" + (item == null ? "null" : item.getType()) + ", stat=" + stat.name() + ", value=" + value + "}");

		if (!ENABLED_WORLDS.contains(WorldGroup.of(player)))
			return;

		if (!stat.isToolApplicable(item))
			return;

		final StatItem statItem = new StatItem(item).increaseStat(stat, value);

		final int slot = statItem.find(player);
		if (slot >= 0) {
			final ItemStack itemInSlot = player.getInventory().getItem(slot);
			if (itemInSlot != null)
				if (itemInSlot.getType() == item.getType()) {
					player.getInventory().setItem(slot, statItem.update());
					player.updateInventory(); // TODO remove?
					Nexus.log("StatTrack item " + item.getType() + " updated in slot " + slot);
				} else
					Nexus.warn("Could not update StatTrack item - slot " + slot + " is not correct type (" + camelCase(itemInSlot.getType()) + " != " + camelCase(item.getType()) + ")");
			else
				Nexus.warn("Could not update StatTrack item - slot " + slot + " is null");
		} else
			Nexus.warn("Could not update StatTrack item - " + camelCase(item.getType()) + " not found in inventory");
	}

}
