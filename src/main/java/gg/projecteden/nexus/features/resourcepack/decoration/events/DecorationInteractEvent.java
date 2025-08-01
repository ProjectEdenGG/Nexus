package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter
public class DecorationInteractEvent extends DecorationEvent {
	private final InteractType interactType;
	private final Block clickedBlock;

	public DecorationInteractEvent(Player player, Block block, Decoration decoration, InteractType type) {
		super(player, decoration);
		this.interactType = type;
		this.clickedBlock = block;
	}

	public enum InteractType {
		RIGHT_CLICK,
		LEFT_CLICK,
		;
	}

	public DecorationType getDecorationType() {
		return DecorationType.of(this.decoration.getConfig());
	}

}
