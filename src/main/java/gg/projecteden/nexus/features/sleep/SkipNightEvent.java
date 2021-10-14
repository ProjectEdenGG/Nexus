package gg.projecteden.nexus.features.sleep;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Data
@RequiredArgsConstructor
public class SkipNightEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final World world;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}
}
