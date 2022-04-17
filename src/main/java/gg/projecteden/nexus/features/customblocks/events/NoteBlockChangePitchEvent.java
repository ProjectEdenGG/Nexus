package gg.projecteden.nexus.features.customblocks.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;

// TODO: implement
public class NoteBlockChangePitchEvent extends NoteBlockEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	public NoteBlockChangePitchEvent(Block block) {
		super(block);
	}
}
