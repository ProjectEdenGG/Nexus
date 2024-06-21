package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds;

import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.frogger.Frogger;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.reflection.Reflection;
import gg.projecteden.nexus.utils.Timer;

public class Fairgrounds {

	private static final String fairgroundsId = "Fairgrounds.";

	public Fairgrounds() {
		new Timer(Pugmas24.timerId + fairgroundsId + "Rides", Rides::new);
		new Timer(Pugmas24.timerId + fairgroundsId + "Frogger", Frogger::new);
		new Timer(Pugmas24.timerId + fairgroundsId + "Reflection", Reflection::new);
		// TODO: MINIGOLF
	}
}
