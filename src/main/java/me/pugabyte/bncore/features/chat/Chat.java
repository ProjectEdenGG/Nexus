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

}
