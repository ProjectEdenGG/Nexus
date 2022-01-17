package gg.projecteden.nexus.models.afk.events;

import gg.projecteden.nexus.models.afk.AFKUser;

public class NotAFKEvent extends AFKEvent {

	public NotAFKEvent(AFKUser user) {
		super(user);
	}

}
