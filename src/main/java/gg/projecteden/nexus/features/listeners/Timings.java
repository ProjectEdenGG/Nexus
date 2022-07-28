package gg.projecteden.nexus.features.listeners;

import co.aikar.timings.event.TimingsGenerateReportEvent;
import co.aikar.timings.event.TimingsMessageEvent;
import co.aikar.timings.event.TimingsModifyEvent;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static gg.projecteden.nexus.utils.StringUtils.getPrefix;

public class Timings implements Listener {

	@EventHandler
	public void on(TimingsMessageEvent event) {
		if (AdventureUtils.asPlainText(event.getMessage()).contains("View Timings Report"))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(TimingsGenerateReportEvent event) {
		Broadcast.staffIngame().prefix("Timings").message(new JsonBuilder("&3Report generated, click to view").url(event.getPaste())).send();
		Broadcast.staffDiscord().prefix("Timings").message(event.getPaste()).send();
	}

	@EventHandler
	public void on(TimingsModifyEvent event) {
		event.setCancelled(true);
		event.getAudience().forEach(commandSender ->
			PlayerUtils.send(commandSender, getPrefix("Timings") + "&cModifying timings settings is not allowed"));
	}

}
