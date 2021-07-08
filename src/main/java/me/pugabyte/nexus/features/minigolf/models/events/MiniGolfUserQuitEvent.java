package me.pugabyte.nexus.features.minigolf.models.events;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.features.minigolf.models.MiniGolfUser;
import org.bukkit.event.Cancellable;

public class MiniGolfUserQuitEvent extends MiniGolfUserEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	public MiniGolfUserQuitEvent(MiniGolfUser user) {
		super(user);
	}
}
