package me.pugabyte.bncore.utils;

import me.pugabyte.bncore.BNCore;

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

	Time(int value) {
		this.value = value;
	}

	public int get() {
		return value;
	}

	public int x(int multiplier) {
		return value * multiplier;
	}

	public static class Timer {
		private static final int IGNORE = 50;

		public Timer(String id, Runnable runnable) {
			long startTime = System.currentTimeMillis();

			runnable.run();

			long duration = System.currentTimeMillis() - startTime;
			if (duration > IGNORE)
				BNCore.log("[Timer] " + id + " took " + duration + "ms");
		}
	}
}
