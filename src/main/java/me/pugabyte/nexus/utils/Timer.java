package me.pugabyte.nexus.utils;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;

public class Timer {
	private static final int IGNORE = 2000;

	@Getter
	private final long duration;

	public Timer(String id, Runnable runnable) {
		long startTime = System.currentTimeMillis();

		runnable.run();

		duration = System.currentTimeMillis() - startTime;
		if (duration >= 1)
			if (Nexus.isDebug() || duration > IGNORE)
				Nexus.log("[Timer] " + id + " took " + duration + "ms");
	}

}
