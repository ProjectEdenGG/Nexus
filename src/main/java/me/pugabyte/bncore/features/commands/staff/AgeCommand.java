package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.Nerd;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AgeCommand extends CustomCommand {

	public AgeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void player(Nerd nerd) {
		if (arg(1).equalsIgnoreCase("bn") || arg(1).equalsIgnoreCase("bearnation") || arg(1).equalsIgnoreCase("server")) {
			bn();
			return;
		}
		try {
			int year = LocalDate.now().getYear() - nerd.getBirthday().getYear();
			send(PREFIX + nerd.getName() + " is " + year + " years old.");
		} catch (Exception e) {
			send(PREFIX + "That player does not have a set birthday");
		}

	}

	@Path()
	void bn() {
		LocalDateTime bn = LocalDateTime.now().withMonth(6).withDayOfMonth(29).withYear(2015).withHour(12).withMinute(52);
		Duration age = Duration.between(bn, LocalDateTime.now());
		send("&3Bear Nation was born on &eJune 29th, 2015&3, at &e12:52 PM ET");
		send("&3That makes it...");
		line();

		double dogYears, years, months, weeks, days, hours, minutes, seconds;
		DecimalFormat format = new DecimalFormat("###,###,##0.00");
		format.setRoundingMode(RoundingMode.UP);

		seconds = age.getSeconds();
		minutes = seconds / 60.0;
		hours = minutes / 60;
		days = hours / 24;
		weeks = days / 7;
		months = days / 30.42;
		years = days / 365;
		dogYears = years * 7;

		send("&e" + format.format(dogYears) + " &3dog years old");
		send("&e" + format.format(years) + " &3years old");
		send("&e" + format.format(months) + " &3months old");
		send("&e" + format.format(weeks) + " &3weeks old");
		send("&e" + format.format(days) + " &3days old");
		send("&e" + format.format(hours) + " &3hours old");
		send("&e" + format.format(minutes) + " &3minutes old");
		send("&e" + format.format(seconds) + " &3seconds old");
	}
}
