package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import lombok.Getter;

public class MiniGolfUserEvent extends MiniGolfEvent {
	@Getter
	protected MiniGolfUser user;

	public MiniGolfUserEvent(final MiniGolfUser user) {
		this.user = user;
	}
}
