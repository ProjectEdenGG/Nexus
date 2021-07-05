package me.pugabyte.nexus.features.minigolf.models.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class MiniGolfUserJoinEvent extends MiniGolfUserEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;
}
