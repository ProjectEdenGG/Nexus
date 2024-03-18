package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class DecorationRotateEvent extends DecorationInteractEvent {

	public DecorationRotateEvent(Player player, Block block, Decoration decoration, InteractType type) {
		super(player, block, decoration, type);
	}
}
