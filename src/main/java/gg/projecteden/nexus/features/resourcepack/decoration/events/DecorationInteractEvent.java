package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class DecorationInteractEvent extends DecorationEvent {
	@Getter
	private final InteractType interactType;
	@Getter
	private final Block clickedBlock;

	public DecorationInteractEvent(Player player, Decoration decoration, Block block, InteractType type) {
		super(player, decoration);
		this.interactType = type;
		this.clickedBlock = block;
	}

	public enum InteractType {
		RIGHT_CLICK,
		LEFT_CLICK,
		;
	}

}
