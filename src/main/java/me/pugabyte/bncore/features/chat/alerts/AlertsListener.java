package me.pugabyte.bncore.features.chat.alerts;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.PrivateChannel;
import me.pugabyte.bncore.features.chat.models.events.DiscordChatEvent;
import me.pugabyte.bncore.features.chat.models.events.MinecraftChatEvent;
import me.pugabyte.bncore.features.chatold.herochat.HerochatAPI;
import me.pugabyte.bncore.models.alerts.Alerts;
import me.pugabyte.bncore.models.alerts.AlertsService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class AlertsListener implements Listener {

	public AlertsListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onChat(MinecraftChatEvent event) {
		if (event.getChannel() instanceof PrivateChannel)
			event.getRecipients().forEach(Chatter::playSound);
		else
			tryAlerts(event.getRecipients(), event.getMessage());
	}

	@EventHandler
	public void onDiscordMessage(DiscordChatEvent event) {
		tryAlerts(event.getRecipients(), event.getMessage());
	}

	public void tryAlerts(Set<Chatter> recipients, String message) {
		List<String> uuids = recipients.stream().map(Chatter::getUuid).collect(Collectors.toList());
		new AlertsService().getAll(uuids).forEach(alerts -> alerts.tryAlerts(message));
	}

	//<editor-fold desc="Herochat">
	public void tryAlertsOld(List<String> uuids, String message) {
		new AlertsService().getAll(uuids).forEach(alerts -> alerts.tryAlerts(message));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChat(com.dthielke.herochat.ChannelChatEvent event) {
		String channelName = event.getChannel().getName();
		String message = event.getMessage();
		Player sender = event.getSender().getPlayer();
		List<com.dthielke.herochat.Chatter> recipients = HerochatAPI.getRecipients(event.getSender(), event.getChannel());
		int count = 0;

		try {
			if (event.getResult() == com.dthielke.herochat.Chatter.Result.ALLOWED) {
				if (channelName.toLowerCase().contains("convo")) {
					recipients.forEach(chatter -> ((Alerts) new AlertsService().get(chatter.getPlayer())).playSound());
				} else {
					List<String> uuids = recipients.stream()
							.map(recipient -> recipient.getPlayer().getUniqueId().toString())
							.collect(Collectors.toList());
					tryAlertsOld(uuids, message);

					if (channelName.matches("(Global|Broadcast|Staff|Operator|Admin)")) {
						count++;
					}

					for (com.dthielke.herochat.Chatter chatter : recipients) {
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
	//</editor-fold>

}
