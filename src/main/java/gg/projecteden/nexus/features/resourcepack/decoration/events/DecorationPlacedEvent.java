package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DecorationPlacedEvent extends DecorationEvent {
	@Getter
	private final ItemStack item;
	@Getter
	private final BlockFace attachedFace;
	@Getter
	private final ItemFrameRotation rotation;
	@Getter
	private final Location location;

	public DecorationPlacedEvent(Player player, Decoration decoration, ItemStack item, BlockFace attachedFace, ItemFrameRotation rotation, Location location) {
		super(player, decoration);
		this.item = item;
		this.attachedFace = attachedFace;
		this.rotation = rotation;
		this.location = location;
	}
}
