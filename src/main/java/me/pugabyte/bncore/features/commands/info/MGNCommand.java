package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.models.geoip.GeoIPService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.getNumberSuffix;

public class MGNCommand extends CustomCommand {

	public MGNCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		ZoneId zoneId = ZoneId.systemDefault();
		if (isPlayer()) {
			GeoIP geoIp = new GeoIPService().get(player());
			zoneId = ZoneId.of(geoIp.getTimezone().getId());
		}

		ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId);
		ZonedDateTime next = Minigames.getNextMGN().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId);

		if (now.isAfter(next)) {
			line();
			send("&3Minigame night is happening right now! Join with &e/gl");
			return;
		}

		long days, hours, minutes;
		days = ChronoUnit.DAYS.between(now, next);
		hours = ChronoUnit.HOURS.between(now, next) % 24;
		minutes = 60 - now.getMinute();

		String until = "";
		if (days > 0)
			until += days + " day" + ((days <= 1) ? "" : "s") + ", ";
		if (hours > 0)
			until += hours + " hour" + ((hours <= 1) ? "" : "s") + ", ";

		until += ((hours > 0 || days > 0) ? "and " : "") + minutes + " minute" + ((minutes == 1) ? "" : "s");

		line();
		send("&3The next &eMinigame Night &3will be hosted on &e" + camelCase(next.getDayOfWeek().name()) + ", " + camelCase(next.getMonth().name()) +
				" " + getNumberSuffix(next.getDayOfMonth()) + "&3 at &e" + next.format(DateTimeFormatter.ofPattern("h:mm a z"))
				+ "&3. That is in &e" + until);
	}

}
