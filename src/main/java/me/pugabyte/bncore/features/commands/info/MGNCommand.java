package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class MGNCommand extends CustomCommand {

	public MGNCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		LocalDateTime dateTime = LocalDateTime.now();
		LocalTime time = dateTime.toLocalTime();
		LocalDateTime nextMGM = dateTime.withHour(16).withMinute(0).withSecond(0);
		if (dateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
			if (time.isAfter(LocalTime.parse("16:00:00")) && time.isBefore(LocalTime.parse("18:00:00"))) {
				line();
				send("&3Minigame night is happening right now! Join with &e/gl");
				return;
			}
		} else nextMGM = nextMGM.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

		long days, hours, minutes;
		days = ChronoUnit.DAYS.between(dateTime, nextMGM);
		hours = ChronoUnit.HOURS.between(dateTime, nextMGM) % 24;
		minutes = 60 - dateTime.getMinute();

		String until = "";
		if (days > 0) {
			until += days + " day" + ((days <= 1) ? "" : "s") + ", ";
		}
		if (hours > 0) {
			until += hours + " hour" + ((hours <= 1) ? "" : "s") + ", ";
		}
		until += ((hours > 0 || days > 0) ? "and " : "") + minutes + " minute" + ((minutes == 1) ? "" : "s");

		line();
		send("&3The next &eMinigame Night &3will be hosted on &eSaturday, " + nextMGM.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + Utils.getNumberSuffix(nextMGM.getDayOfMonth()) + "&3 at &e4:00 PM &3EST. That is in &e" + until);
	}

}
