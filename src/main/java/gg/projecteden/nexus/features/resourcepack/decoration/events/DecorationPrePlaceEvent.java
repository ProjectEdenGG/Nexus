package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DecorationPrePlaceEvent extends DecorationEvent {
	@Getter
	@Setter
	private ItemStack item;
	@Getter
	@Setter
	private BlockFace attachedFace;
	@Getter
	@Setter
	private ItemFrameRotation rotation;

	public DecorationPrePlaceEvent(Player player, Decoration decoration, ItemStack item, BlockFace attachedFace, ItemFrameRotation rotation) {
		super(player, decoration);
		this.item = item;
		this.attachedFace = attachedFace;
		this.rotation = rotation;
	}

	public Location getLocation() {
		return decoration.getItemFrame().getLocation();
	}
}
