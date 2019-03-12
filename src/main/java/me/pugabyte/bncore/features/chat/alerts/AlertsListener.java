package me.pugabyte.bncore.features.chat.alerts;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.alerts.models.DiscordMessageEvent;
import me.pugabyte.bncore.features.chat.herochat.HerochatAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

import static me.pugabyte.bncore.features.chat.Chat.alerts;

public class AlertsListener implements Listener {

	AlertsListener() {
		BNCore.registerListener(this);
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onChat(ChannelChatEvent event) {
		String channelName = event.getChannel().getName();
		String message = event.getMessage();
		Player sender = event.getSender().getPlayer();
		List<Chatter> recipients = HerochatAPI.getRecipients(event.getSender(), event.getChannel());
		int count = 0;

		try {
			if (event.getResult().toString().equals("ALLOWED")) {
				if (channelName.matches("(Global|Broadcast|Staff|Operator|Admin)")) {
					count++;
				}
				for (Chatter chatter : recipients) {
					Player loopPlayer = chatter.getPlayer();
					alerts.tryAlerts(message, loopPlayer);

					if (!BNCore.isVanished(loopPlayer)) {
						count++;
					} else if (sender.hasPermission("vanish.see")) {
						count++;
					} else if (channelName.toLowerCase().contains("convo")) {
						count++;
					}
				}
			}
			if (count == 0) {
				String warning = "§eNo one can hear you! Type §c/ch g §eto talk globally.";
				BNCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BNCore.getInstance(), () ->
								sender.getPlayer().sendMessage(warning)
						, 2L);
			}
		} catch (Exception e) {
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDiscordMessage(DiscordMessageEvent e) {
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.hasPermission(e.getPermission()))
				alerts.tryAlerts(e.getMessage(), player);
	}

}
