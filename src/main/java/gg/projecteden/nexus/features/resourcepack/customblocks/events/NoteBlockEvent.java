package gg.projecteden.nexus.features.resourcepack.customblocks.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class NoteBlockEvent extends Event implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;
	private static final HandlerList handlers = new HandlerList();

	@Getter
	protected NoteBlock noteBlock;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public NoteBlockEvent(final Block block) {
		this.noteBlock = (NoteBlock) block.getBlockData();
	}
}
