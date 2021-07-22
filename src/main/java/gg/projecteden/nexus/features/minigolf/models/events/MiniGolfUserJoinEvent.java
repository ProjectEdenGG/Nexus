package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.features.minigolf.models.MiniGolfUser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class MiniGolfUserJoinEvent extends MiniGolfUserEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	public MiniGolfUserJoinEvent(MiniGolfUser user) {
		super(user);
	}
}
