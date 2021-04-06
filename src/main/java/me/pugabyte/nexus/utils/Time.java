package me.pugabyte.nexus.utils;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.Nexus;

@AllArgsConstructor
public enum Time {
	TICK(1),
	SECOND(TICK.get() * 20),
	MINUTE(SECOND.get() * 60),
	HOUR(MINUTE.get() * 60),
	DAY(HOUR.get() * 24),
	WEEK(DAY.get() * 7),
	MONTH(DAY.get() * 30),
	YEAR(DAY.get() * 365);

	private final int value;

	public int get() {
		return value;
	}

	public int x(int multiplier) {
		return value * multiplier;
	}

	public int x(double multiplier) {
		return (int) (value * multiplier);
	}

	public static class Timer {
		private static final int IGNORE = 2000;

		public Timer(String id, Runnable runnable) {
			long startTime = System.currentTimeMillis();

			runnable.run();

			long duration = System.currentTimeMillis() - startTime;
			if (duration >= 1)
				if (Nexus.isDebug() || duration > IGNORE)
					Nexus.log("[Timer] " + id + " took " + duration + "ms");
		}
	}
}
