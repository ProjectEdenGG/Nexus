package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.utils.Timer;

import java.time.LocalDate;

public class Pugmas21 {
	public static final LocalDate EPOCH = LocalDate.of(2021, 12, 1);
	public static final LocalDate PUGMAS = LocalDate.of(2021, 12, 25);

	public Pugmas21() {
		new Timer("      Events.Pugmas21.CandyCaneCannon", CandyCaneCannon::new);
	}

}
