package gg.projecteden.nexus.features.resourcepack.customblocks.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class NoteBlockChangePitchEvent extends NoteBlockEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;
	@Getter
	protected Player player;


	public NoteBlockChangePitchEvent(Player player, Block block) {
		super(block);
	}
}
