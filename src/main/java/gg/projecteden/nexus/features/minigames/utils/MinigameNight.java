package gg.projecteden.nexus.features.minigames.utils;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import lombok.Data;
import org.bukkit.OfflinePlayer;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@Data
public class MinigameNight {
	private final boolean now;
	private final ZonedDateTime next;
	private final long days, hours, minutes;
	private final String until;
	private final String dateFormatted;
	private final String timeFormatted;

	public MinigameNight() {
		this(ZoneId.systemDefault());
	}

	public MinigameNight(OfflinePlayer player) {
		this(new GeoIPService().get(player));
	}

	public MinigameNight(GeoIP player) {
		this(!GeoIP.exists(player) ? ZoneId.systemDefault() : ZoneId.of(player.getTimezone().getId()));
	}

	public MinigameNight(ZoneId zoneId) {
		ZonedDateTime currentTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId);
		next = getNextMGN().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId);
		now = currentTime.isAfter(next);
		days = ChronoUnit.DAYS.between(currentTime, next);
		hours = ChronoUnit.HOURS.between(currentTime, next) % 24;
		minutes = 60 - currentTime.getMinute();

		String until = "";
		if (days > 0)
			until += days + " day" + ((days <= 1) ? "" : "s") + ", ";
		if (hours > 0)
			until += hours + " hour" + ((hours <= 1) ? "" : "s") + ", ";

		until += ((hours > 0 || days > 0) ? "and " : "") + minutes + " minute" + ((minutes == 1) ? "" : "s");
		this.until = until;

		dateFormatted = StringUtils.camelCase(next.getDayOfWeek().name()) + ", " + StringUtils.camelCase(next.getMonth().name()) + " " + StringUtils.getNumberWithSuffix(next.getDayOfMonth());
		timeFormatted = next.format(DateTimeFormatter.ofPattern("h:mm a z"));
	}

	private static LocalDateTime getNextMGN() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime next;
		if (now.getDayOfWeek().equals(DayOfWeek.SATURDAY) && now.getHour() <= 18)
			next = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
		else
			next = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

		return next.withHour(16).withMinute(0).withSecond(0).withNano(0);
	}

}
