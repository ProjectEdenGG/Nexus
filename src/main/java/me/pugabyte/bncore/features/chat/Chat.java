package me.pugabyte.bncore.features.chat;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.alerts.AlertsFeature;
import me.pugabyte.bncore.features.chat.translator.Translator;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Chat {
	public static AlertsFeature alertsFeature;
	public static Translator translator;

	static {
		PlaceholderAPI.registerPlaceholder(BNCore.getInstance(), "currentchannel", event -> {
			Chatter chatter = Herochat.getChatterManager().getChatter(event.getPlayer());
			if (chatter == null)
				return "&eNone";
			Channel activeChannel = chatter.getActiveChannel();
			if (activeChannel == null)
				return "&eNone";

			return activeChannel.getColor() + activeChannel.getName();
		});

		PlaceholderAPI.registerPlaceholder(BNCore.getInstance(), "vanished", event ->
				String.valueOf(Utils.isVanished(event.getPlayer())));

		PlaceholderAPI.registerPlaceholder(BNCore.getInstance(), "nerds", event ->
				String.valueOf(Bukkit.getOnlinePlayers().stream().filter(target -> Utils.canSee(event.getPlayer(), target)).count()));
	}

	public Chat() {
		Plugin herochat = BNCore.getInstance().getServer().getPluginManager().getPlugin("Herochat");

		BNCore.getInstance().addConfigDefault("localRadius", 500);

		if (herochat != null && herochat.isEnabled()) {
			new ChatListener();
			alertsFeature = new AlertsFeature();
			translator = new Translator();
		}
	}

	public static int getLocalRadius() {
		return BNCore.getInstance().getConfig().getInt("localRadius");
	}

}
