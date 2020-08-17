package me.pugabyte.bncore.features.commands.staff;

import com.mysql.cj.util.StringUtils;
import me.lucko.helper.time.DurationFormatter;
import me.lucko.helper.time.DurationParser;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.util.regex.Pattern;

//@Fallback("litebans")
@Permission("group.moderator")
//@Redirects.Redirect(from = {"/tempban", "/iptempban", "/tempbanip", "/tempipban", "/ipban", "/banip",
//		"/ban-ip" , "/lban", "/lipban", "/tban"}, to = "/bncore:ban")
public class JBanCommand extends CustomCommand {
	private static final String timeRegex = "(([0-9]+) ?(y(?:ear)?s?|mo(?:nth)?s?|w(?:eek)?s?|d(?:ay)?s?|h(?:our|r)?s?|m(?:inute|in)?s?|s(?:econd|ec)?s?) ?)";

	public JBanCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	public void run() {
		fallback();
	}

	@Path("<player> [string...]")
	public void ban(OfflinePlayer offlinePlayer, String arguments) {
		String[] timeReason = validateParameters(arguments);
		String time = timeReason[0];
		String reason = timeReason[1].trim();

		boolean temporary = !StringUtils.isNullOrEmpty(time);

		send("From: /jban " + offlinePlayer.getName() + " " + arguments);  // TODO: REMOVE DEBUG
		if (temporary)
			tempBan(offlinePlayer, time.trim(), reason);
		else
			permBan(offlinePlayer, reason);
		send(); // TODO: REMOVE DEBUG
	}

	private void permBan(OfflinePlayer player, String reason) {
		send("To: /ban " + player.getName() + " " + reason);
	}

	private void tempBan(OfflinePlayer player, String time, String reason) {
		send("To: /tempban " + player.getName() + " " + time + " " + reason);
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
			validated[0] = validateTime2(time);
		validated[1] = reason;

		return validated;
	}

	private String validateTime2(String timeString) {
		Duration duration = DurationParser.parse(timeString);
		long seconds = duration.getSeconds();
		String expanded = DurationFormatter.LONG.format(duration);
		String concise = DurationFormatter.CONCISE.format(duration).replaceFirst("m", "mo");

		Utils.wakka("Debug:");
		Utils.wakka("Seconds: " + seconds);
		Utils.wakka("Long: " + expanded);
		Utils.wakka("Concise: " + concise);
		Utils.wakka("");

		return concise.replaceAll(" ", "");
	}

}
