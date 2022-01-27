package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DecorationDestroyEvent extends DecorationEvent {
	@Getter
	String ownerUUID;

	public DecorationDestroyEvent(Player player, Location origin, Decoration decoration, ItemStack item, String ownerUUID) {
		super(player, origin, decoration, item);
		this.ownerUUID = ownerUUID;
	}
}
