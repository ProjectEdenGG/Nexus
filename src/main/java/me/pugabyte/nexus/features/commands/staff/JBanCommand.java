package me.pugabyte.nexus.features.commands.staff;

import com.mysql.cj.util.StringUtils;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

// litebans bans for days hours minutes, and only allws you to type in 1 unit, and 20160 minutes --> 14 days

//@Fallback("litebans")
@Permission("group.moderator")
//@Redirect(from = {"/tempban", "/iptempban", "/tempbanip", "/tempipban", "/ipban", "/banip",
//		"/ban-ip" , "/lban", "/lipban", "/tban"}, to = "/nexus:ban")
public class JBanCommand extends CustomCommand {
	private static final String timeRegex = "(([0-9]+(?:\\.[0-9]+)?) ?(y(?:ear)?s?|mo(?:nth)?s?|w(?:eek)?s?|d(?:ay)?s?|h(?:our|r)?s?|m(?!o)(?:inute|in)?s?|s(?:econd|ec)?s?) ?)";

	public JBanCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	public void run() {
		fallback();
	}

	// public void ban(OfflinePlayer offlinePlayer, String arguments) {

	@Path("<player> [string...]")
	public void ban(@Arg(tabCompleter = OfflinePlayer.class) String playerName, String arguments) {
		String[] timeReason = validateParameters(arguments);
		String time = timeReason[0];
		String reason = timeReason[1].trim();

		boolean temporary = !StringUtils.isNullOrEmpty(time);

		send("From: /jban " + playerName + " " + arguments);  // TODO: REMOVE DEBUG
		if (temporary)
			send("To: /tempban " + playerName + " " + time + " " + reason);
		else
			send("To: /ban " + playerName + " " + reason);
		send(); // TODO: REMOVE DEBUG
	}

	private String[] validateParameters(String arguments) {
		Pattern timeRegex_first = Pattern.compile("^" + timeRegex + "+");
		Pattern timeRegex_last = Pattern.compile(timeRegex + "+$");

		String[] validated = new String[2];
		String time = null;
		String reason;

		// If time is first argument
		if (timeRegex_first.matcher(arguments).find()) {
			reason = arguments.replaceAll(timeRegex_first.pattern(), "");
			time = arguments.replaceAll(reason, "");

			// If time is the last argument
		} else if (timeRegex_last.matcher(arguments).find()) {
			reason = arguments.replaceAll(timeRegex_last.pattern(), "");
			time = arguments.replaceAll(reason, "");

			// A time could not be found, so this is not a tempban
		} else
			reason = arguments;

		validated[0] = time;
		if (time != null)
			validated[0] = parseTime(time);
		validated[1] = reason;

		return validated;
	}

	private String parseTime(String timeString) {
		Duration duration = parseDuration(timeString);

		long seconds = duration.getSeconds();
		float secondsInDay = ChronoUnit.DAYS.getDuration().getSeconds();
		float leftover = (seconds - (duration.toDays() * secondsInDay)) / secondsInDay;
		float days = duration.toDays() + leftover;
		DecimalFormat nf = new DecimalFormat("#.00");
		String daysStr = nf.format(days);

		Utils.wakka("Debug:");
		Utils.wakka("Days: " + daysStr);
		Utils.wakka("Leftover: " + leftover);

		// 15 minutes is 0.01d, but any less than that, and it breaks
		// 15 = 0.01
		// 14-8 = 0.01 (because of rounding)
		// >7 = 0.00

		return daysStr + "d";
	}

	private Duration parseDuration(String timeString) {
		String unitsRegex = "(y(?:ear)?s?|mo(?:nth)?s?|w(?:eek)?s?|d(?:ay)?s?|h(?:our|r)?s?|m(?!o)(?:inute|in)?s?|s(?:econd|ec)?s?)";
		String[] units = timeString.trim().split("(?<=" + unitsRegex + ")");

		String regex_years = "y(?:ear)?s?";
		String regex_months = "mo(?:nth)?s?";
		String regex_weeks = "w(?:eek)?s?";
		String regex_days = "d(?:ay)?s?";
		String regex_hours = "h(?:our|r)?s?";
		String regex_minutes = "m(?!o)(?:inute|in)?s?";
		String regex_seconds = "s(?:econd|ec)?s?";
		String regex_numbers = "([0-9]+(?:\\.[0-9]+)?) ?";
		float totalSeconds = 0;
		for (String unit : units) {
			float amount = 0;
			float seconds = 0;
			if (unit.matches(regex_numbers + regex_years)) {
				amount = Float.parseFloat(unit.replaceAll(regex_years, "").trim());
				seconds = ChronoUnit.YEARS.getDuration().getSeconds();
			} else if (unit.matches(regex_numbers + regex_months)) {
				amount = Float.parseFloat(unit.replaceAll(regex_months, "").trim());
				seconds = ChronoUnit.MONTHS.getDuration().getSeconds();
			} else if (unit.matches(regex_numbers + regex_weeks)) {
				amount = Float.parseFloat(unit.replaceAll(regex_weeks, "").trim());
				seconds = ChronoUnit.WEEKS.getDuration().getSeconds();
			} else if (unit.matches(regex_numbers + regex_days)) {
				amount = Float.parseFloat(unit.replaceAll(regex_days, "").trim());
				seconds = ChronoUnit.DAYS.getDuration().getSeconds();
			} else if (unit.matches(regex_numbers + regex_hours)) {
				amount = Float.parseFloat(unit.replaceAll(regex_hours, "").trim());
				seconds = ChronoUnit.HOURS.getDuration().getSeconds();
			} else if (unit.matches(regex_numbers + regex_minutes)) {
				amount = Float.parseFloat(unit.replaceAll(regex_minutes, "").trim());
				seconds = ChronoUnit.MINUTES.getDuration().getSeconds();
			} else if (unit.matches(regex_numbers + regex_seconds)) {
				amount = Float.parseFloat(unit.replaceAll(regex_seconds, "").trim());
				seconds = 1;
			}

			totalSeconds += (amount * seconds);
		}
		return Duration.of((long) totalSeconds, ChronoUnit.SECONDS);
	}

}
