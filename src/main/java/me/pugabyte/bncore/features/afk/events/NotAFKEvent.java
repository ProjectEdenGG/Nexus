package me.pugabyte.bncore.features.afk.events;

import me.pugabyte.bncore.models.afk.AFKPlayer;

public class NotAFKEvent extends AFKEvent {

	public NotAFKEvent(AFKPlayer player) {
		super(player);
	}

}
