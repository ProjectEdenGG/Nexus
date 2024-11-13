package gg.projecteden.nexus.features.listeners.events;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A variation of the redstone event with a source block.
 */
public class SourcedBlockRedstoneEvent extends BlockRedstoneEvent {

	@Getter
	protected final Block source;
	@Getter
	private static final HandlerList handlerList = new HandlerList();

	public SourcedBlockRedstoneEvent(Block source, Block block, int old, int n) {
		super(block, old, n);
		this.source = source;
	}

	public boolean hasChanged() {
		return getOldCurrent() != getNewCurrent();
	}

	public boolean isMinor() {
		return !hasChanged() || wasOn() == isOn();
	}

	public boolean isOn() {
		return getNewCurrent() > 0;
	}

	public boolean wasOn() {
		return getOldCurrent() > 0;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

}
