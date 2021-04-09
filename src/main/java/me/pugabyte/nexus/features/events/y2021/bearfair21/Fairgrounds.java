package me.pugabyte.nexus.features.events.y2021.bearfair21;

import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.utils.Time.Timer;

public class Fairgrounds {
	public Fairgrounds() {
		new Timer("      Minigolf", MiniGolf::new);
	}
}
