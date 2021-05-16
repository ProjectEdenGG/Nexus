package me.pugabyte.nexus.features.events.y2021.pride21;

import me.pugabyte.nexus.utils.StringUtils;

import java.time.LocalDate;

public class Pride21 {
	public static final String PREFIX = StringUtils.getPrefix("Pride");
	public static final String REGION = "pride21";

	public static boolean QUESTS_ENABLED() {
		return LocalDate.now().isBefore(LocalDate.of(2021, 7, 1));
	}

	public Pride21() {
		new Quests();
	}
}
