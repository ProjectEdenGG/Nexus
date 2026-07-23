package gg.projecteden.nexus.features.tournaments;

import gg.projecteden.api.common.exceptions.EdenException;

import java.time.YearMonth;
import java.util.concurrent.atomic.AtomicInteger;

public class ChallongeRateLimiter {

	private static final int MONTHLY_LIMIT = 5_000;
	private static final int WARNING_THRESHOLD = 4_500;

	private static YearMonth currentMonth = YearMonth.now();
	private static final AtomicInteger requestsThisMonth = new AtomicInteger();

	public static void checkAndIncrement() {
		rolloverIfNeeded();

		int count = requestsThisMonth.incrementAndGet();

		if (count == WARNING_THRESHOLD) {
			System.out.println("[Challonge] WARNING: Approaching monthly API limit (" + count + ")");
		}

		if (count > MONTHLY_LIMIT) {
			throw new EdenException(
				"Challonge API limit exceeded (" + count + "/" + MONTHLY_LIMIT + "). Requests blocked."
			);
		}
	}

	private static void rolloverIfNeeded() {
		YearMonth now = YearMonth.now();
		if (!now.equals(currentMonth)) {
			currentMonth = now;
			requestsThisMonth.set(0);
		}
	}

	public static int getUsage() {
		rolloverIfNeeded();
		return requestsThisMonth.get();
	}
}
