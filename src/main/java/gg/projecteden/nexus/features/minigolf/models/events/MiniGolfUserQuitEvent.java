package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class MiniGolfUserQuitEvent extends MiniGolfUserEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	public MiniGolfUserQuitEvent(MiniGolfUser user) {
		super(user);
	}
}
