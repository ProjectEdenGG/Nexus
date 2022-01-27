package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DecorationPlaceEvent extends DecorationEvent {

	public DecorationPlaceEvent(Player player, Location origin, Decoration decoration, ItemStack item) {
		super(player, origin, decoration, item);
	}
}
