package gg.projecteden.nexus.features.godmode.events;

import gg.projecteden.nexus.models.godmode.Godmode;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Data
@RequiredArgsConstructor
public class GodmodeEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();
	@NonNull
	protected final Godmode user;

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
