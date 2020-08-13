package me.pugabyte.bncore.features.commands.staff;

import com.mysql.cj.util.StringUtils;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

import java.util.regex.Pattern;

//@Fallback("litebans")
@Permission("group.moderator")
//@Redirects.Redirect(from = {"/tempban", "/iptempban", "/tempbanip", "/tempipban", "/ipban", "/banip",
//		"/ban-ip" , "/lban", "/lipban", "/tban"}, to = "/bncore:ban")
public class JBanCommand extends CustomCommand {

	public JBanCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	public void run() {
		fallback();
	}

	@Path("<player> [string...]")
	public void ban(OfflinePlayer offlinePlayer, String arguments) {
		String[] timeReason = validate(arguments);
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

	private String[] validate(String arguments) {
		String timeRegex = "(([0-9]+) ?(years?|y|months?|mo|weeks?|w|hours?|h|days?|d|minutes?|m|seconds?|s))";

		Pattern timeRegex_first = Pattern.compile("^" + timeRegex);
		Pattern timeRegex_last = Pattern.compile(timeRegex + "$");

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
		validated[1] = reason;

		return validated;
	}


	/*
		ban player
		ban player reason
		ban player time

		ban player time reason
		ban player reason time


		time regex =
			first parameter:
				^([0-9]+) ?(years?|y|months?|mo|weeks?|w|hours?|h|days?|d|minutes?|m|seconds?|s)
			last parameter:
				([0-9]+) ?(years?|y|months?|mo|weeks?|w|hours?|h|days?|d|minutes?|m|seconds?|s)$

		remaining = reason
	 */


}
