package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.Data;
import lombok.Getter;

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
	@Description("View the age of a player")
	void player(Nerd nerd) {
		if (arg(1).equalsIgnoreCase("eden") || arg(1).equalsIgnoreCase("projecteden") || arg(1).equalsIgnoreCase("server")) {
			server();
			return;
		}

		try {
			int year = nerd.getBirthday().until(LocalDate.now()).getYears();
			send(PREFIX + Nickname.of(nerd) + " is &e" + year + "&3 years old.");
		} catch (Exception ex) {
			send(PREFIX + "That player does not have a set birthday");
		}
	}

	@Data
	public static class ServerAge {
		@Getter
		public static final LocalDateTime EPOCH = LocalDateTime.now().withMonth(6).withDayOfMonth(29).withYear(2015).withHour(12).withMinute(52);
		private final double dogYears, years, months, weeks, days, hours, minutes, seconds;

		public ServerAge() {
			Duration age = Duration.between(EPOCH, LocalDateTime.now());
			seconds = age.getSeconds();
			minutes = seconds / 60;
			hours = minutes / 60;
			days = hours / 24;
			weeks = days / 7;
			months = days / 30.42;
			years = days / 365;
			dogYears = years * 7;
		}

		private static final DecimalFormat format = new DecimalFormat("###,###,##0.00");
		static { format.setRoundingMode(RoundingMode.UP); }

		public static String format(double value) {
			return format.format(value);
		}
	}

	@Path
	@Description("View the age of the server")
	void server() {
		ServerAge serverAge = new ServerAge();

		send("&3Project Eden was born on &eJune 29th, 2015&3, at &e12:52 PM ET");
		send("&3That makes it...");
		line();
		send("&e" + ServerAge.format(serverAge.getDogYears()) + " &3dog years old");
		send("&e" + ServerAge.format(serverAge.getYears()) + " &3years old");
		send("&e" + ServerAge.format(serverAge.getMonths()) + " &3months old");
		send("&e" + ServerAge.format(serverAge.getWeeks()) + " &3weeks old");
		send("&e" + ServerAge.format(serverAge.getDays()) + " &3days old");
		send("&e" + ServerAge.format(serverAge.getHours()) + " &3hours old");
		send("&e" + ServerAge.format(serverAge.getMinutes()) + " &3minutes old");
		send("&e" + ServerAge.format(serverAge.getSeconds()) + " &3seconds old");
	}
}
