package me.pugabyte.bncore.features.chat.alerts;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.features.chat.alerts.models.DiscordMessageEvent;
import me.pugabyte.bncore.features.chat.herochat.HerochatAPI;
import me.pugabyte.bncore.models.alerts.Alerts;
import me.pugabyte.bncore.models.alerts.AlertsService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.Utils.colorize;
import static me.pugabyte.bncore.features.chat.Chat.alertsFeature;

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
				if (channelName.toLowerCase().contains("convo")) {
					recipients.forEach(chatter -> ((Alerts) new AlertsService().get(chatter.getPlayer())).playSound());
				} else {
					List<String> uuids = recipients.stream()
							.map(recipient -> recipient.getPlayer().getUniqueId().toString())
							.collect(Collectors.toList());
					alertsFeature.tryAlerts(uuids, message);

					if (channelName.matches("(Global|Broadcast|Staff|Operator|Admin)")) {
						count++;
					}

					for (Chatter chatter : recipients) {
						if (!Utils.isVanished(chatter.getPlayer())) {
							count++;
						} else if (sender.hasPermission("vanish.see")) {
							count++;
						}
					}

					if (count == 0) {
						String warning = colorize("&eNo one can hear you! Type &c/ch g &eto talk globally.");
						BNCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BNCore.getInstance(), () ->
										sender.getPlayer().sendMessage(warning)
								, 2L);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDiscordMessage(DiscordMessageEvent e) {
		List<String> uuids = Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.hasPermission(e.getPermission()))
				.map(player -> player.getUniqueId().toString())
				.collect(Collectors.toList());

		alertsFeature.tryAlerts(uuids, e.getMessage());
	}

}
