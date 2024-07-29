package gg.projecteden.nexus.features.chat.events;

import gg.projecteden.nexus.models.chat.Channel;
import gg.projecteden.nexus.models.chat.Chatter;
import lombok.Data;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Data
public class ChannelChangeEvent extends Event {
	private final Chatter chatter;
	private final Channel previousChannel, newChannel;

	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
