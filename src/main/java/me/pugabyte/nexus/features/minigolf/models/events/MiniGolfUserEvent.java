package me.pugabyte.nexus.features.minigolf.models.events;

import lombok.Getter;
import me.pugabyte.nexus.features.minigolf.models.MiniGolfUser;

public class MiniGolfUserEvent extends MiniGolfEvent {
	@Getter
	protected MiniGolfUser user;

	public MiniGolfUserEvent(final MiniGolfUser user) {
		this.user = user;
	}
}
