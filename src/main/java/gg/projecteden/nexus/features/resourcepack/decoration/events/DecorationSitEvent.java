package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter
@Setter
public class DecorationSitEvent extends DecorationInteractEvent {
	private Rotation rotation;
	private final Block block;

	public DecorationSitEvent(Player player, Decoration decoration, Rotation rotation, Block block) {
		super(player, decoration);
		this.rotation = rotation;
		this.block = block;
	}

	public Seat getSeat() {
		return (Seat) decoration.getConfig();
	}

}
