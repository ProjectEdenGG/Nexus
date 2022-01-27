package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DecorationSitEvent extends DecorationInteractEvent {
	@Getter
	@Setter
	Rotation rotation;
	@Getter
	Seat seat;
	@Getter
	Block block;

	public DecorationSitEvent(Player player, Location origin, Seat seat, ItemStack item, Rotation rotation, Block block) {
		super(player, origin, (Decoration) seat, item);
		this.seat = seat;
		this.rotation = rotation;
		this.block = block;
	}
}
