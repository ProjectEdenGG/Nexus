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

	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
