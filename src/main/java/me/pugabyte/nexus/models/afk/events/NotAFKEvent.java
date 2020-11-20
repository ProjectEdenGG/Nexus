package me.pugabyte.nexus.models.afk.events;

import me.pugabyte.nexus.models.afk.AFKPlayer;

public class NotAFKEvent extends AFKEvent {

	public NotAFKEvent(AFKPlayer player) {
		super(player);
	}

}
