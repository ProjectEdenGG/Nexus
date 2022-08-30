package gg.projecteden.nexus.features.fakenpc.events;

import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class FakeNPCEvent extends Event {
	@Getter
	FakeNPC npc;

	public FakeNPCEvent(FakeNPC npc) {
		this.npc = npc;
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}
}
