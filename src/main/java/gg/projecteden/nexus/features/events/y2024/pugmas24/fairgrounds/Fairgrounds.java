package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds;

import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.frogger.Frogger;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.reflection.Reflection;
import org.bukkit.Location;

public class Fairgrounds {
	public static final Location minigolfAnimationLoc = Pugmas24.get().location(-755, 71, -2915);

	public Fairgrounds() {
		new Rides();
		new Frogger();
		new Reflection();
		// TODO: MINIGOLF
	}
}
