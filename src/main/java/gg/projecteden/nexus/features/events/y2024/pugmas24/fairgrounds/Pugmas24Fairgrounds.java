package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds;

import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.frogger.Pugmas24Frogger;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.reflection.Pugmas24Reflection;
import org.bukkit.Location;

public class Pugmas24Fairgrounds {
	public static final Location minigolfAnimationLoc = Pugmas24.get().location(-755, 71, -2915);

	public Pugmas24Fairgrounds() {
		new Pugmas24Rides();
		new Pugmas24Frogger();
		new Pugmas24Reflection();
		// TODO: MINIGOLF
	}
}
