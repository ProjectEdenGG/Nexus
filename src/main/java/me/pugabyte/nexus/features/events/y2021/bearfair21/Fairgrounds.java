package me.pugabyte.nexus.features.events.y2021.bearfair21;

import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Archery;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Frogger;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Interactables;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Reflection;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Seeker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.utils.TimeUtils.Timer;

public class Fairgrounds {
	public Fairgrounds() {
		new Timer("      Interactables", Interactables::new);
		new Timer("      Minigolf", MiniGolf::new);
		new Timer("      Archery", Archery::new);
		new Timer("      Frogger", Frogger::new);
		new Timer("      Seeker", Seeker::new);
		new Timer("      Reflection", Reflection::new);
	}
}
