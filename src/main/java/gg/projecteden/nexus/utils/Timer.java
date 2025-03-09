package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import lombok.Getter;

public class Timer {
	private static final int IGNORE = 1000;

	@Getter
	private final long duration;

	public Timer(String id, Runnable runnable) {
		this(id, null, runnable);
	}

	public Timer(String id, Boolean debug, Runnable runnable) {
		long startTime = System.currentTimeMillis();

		runnable.run();

		duration = System.currentTimeMillis() - startTime;

		if (debug == null ? Debug.isEnabled() : debug || duration > IGNORE)
			Nexus.log("[Timer] " + id + " took " + duration + "ms");
	}

}
