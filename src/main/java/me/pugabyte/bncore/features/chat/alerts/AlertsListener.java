package me.pugabyte.bncore.features.chat.alerts;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.events.DiscordChatEvent;
import me.pugabyte.bncore.features.chat.events.MinecraftChatEvent;
import me.pugabyte.bncore.models.alerts.AlertsService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PrivateChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AlertsListener implements Listener {

	public AlertsListener() {
		BNCore.registerListener(this);
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(MinecraftChatEvent event) {
		if (event.getChannel() instanceof PrivateChannel)
			event.getRecipients().stream().filter(chatter -> !chatter.equals(event.getChatter())).forEach(Chatter::playSound);
		else
			tryAlerts(event.getRecipients(), event.getMessage());
	}

	@EventHandler(ignoreCancelled = true)
	public void onDiscordMessage(DiscordChatEvent event) {
		tryAlerts(event.getRecipients(), event.getMessage());
	}

	public void tryAlerts(Set<Chatter> recipients, String message) {
		List<String> uuids = recipients.stream().map(chatter -> chatter.getUuid().toString()).collect(Collectors.toList());
		new AlertsService().getAll(uuids).forEach(alerts -> alerts.tryAlerts(message));
	}

}
