package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.alerts.Alerts;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat {
	public static Alerts alerts;

	public Chat() {
		Plugin herochat = BNCore.getInstance().getServer().getPluginManager().getPlugin("Herochat");

		if (herochat != null && herochat.isEnabled()) {
			new ChatListener();
			alerts = new Alerts();
		}
	}

	public static String jsonFixColors(String message) {
		boolean ignoringAllColors = false;

		Pattern pattern = Pattern.compile("(§[a-r0-9])+");
		Pattern innerPattern = Pattern.compile("(§[a-f0-9]((§[g-z])+|))");
		Matcher matcher = pattern.matcher(message);
		while (matcher.find()) {
			String group = matcher.group();
			Matcher innerMatcher = innerPattern.matcher(group);
			String last = "";
			while (innerMatcher.find()) {
				last = innerMatcher.group();
			}

			if (last.length() != 0) {

				if (last.endsWith("r")) {
					last = "§r";
				}

				if (!ignoringAllColors) {
					if (last.endsWith("r")) {
						ignoringAllColors = true;
					}
				} else {
					if (last.endsWith("f") || last.endsWith("r")) {
						last = "";
					} else {
						ignoringAllColors = false;
					}
				}

				message = message.replaceFirst(group, last);
			}
		}

		return message;
	}

}
