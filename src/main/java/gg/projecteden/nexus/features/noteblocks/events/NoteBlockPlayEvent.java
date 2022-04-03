package gg.projecteden.nexus.features.noteblocks.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;

public class NoteBlockPlayEvent extends NoteBlockEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	public NoteBlockPlayEvent(Block block) {
		super(block);
	}
}
