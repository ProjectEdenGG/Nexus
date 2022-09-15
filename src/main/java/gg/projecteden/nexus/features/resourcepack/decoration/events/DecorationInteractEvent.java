package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import org.bukkit.entity.Player;

public class DecorationInteractEvent extends DecorationEvent {
	@Getter
	private final InteractType interactType;

	public DecorationInteractEvent(Player player, Decoration decoration, InteractType type) {
		super(player, decoration);
		this.interactType = type;
	}

	public enum InteractType {
		RIGHT_CLICK,
		LEFT_CLICK,
		;
	}

}
