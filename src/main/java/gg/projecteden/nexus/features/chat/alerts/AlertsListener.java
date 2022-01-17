package gg.projecteden.nexus.features.chat.alerts;

import gg.projecteden.nexus.features.chat.events.DiscordChatEvent;
import gg.projecteden.nexus.features.chat.events.MinecraftChatEvent;
import gg.projecteden.nexus.models.alerts.Alerts;
import gg.projecteden.nexus.models.alerts.AlertsService;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.PrivateChannel;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Name;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class AlertsListener implements Listener {

	@NotNull
	public Set<Chatter> getEveryoneElse(Chatter origin, Set<Chatter> recipients) {
		return recipients.stream().filter(chatter -> !chatter.equals(origin)).collect(Collectors.toSet());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(MinecraftChatEvent event) {
		Set<Chatter> everyoneElse = getEveryoneElse(event.getChatter(), event.getRecipients());
		if (event.getChannel() instanceof PrivateChannel) {
			everyoneElse.forEach(Chatter::playSound);
		} else
			tryAlerts(everyoneElse, event.getMessage());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDiscordMessage(DiscordChatEvent event) {
		Set<Chatter> everyoneElse = event.getRecipients();
		if (event.getChatter() != null)
			everyoneElse = getEveryoneElse(event.getChatter(), event.getRecipients());
		tryAlerts(everyoneElse, event.getMessage());
	}

	public void tryAlerts(Set<Chatter> recipients, String message) {
		AlertsService service = new AlertsService();
		recipients.forEach(chatter -> service.get(chatter.getUuid()).tryAlerts(message));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		final AlertsService service = new AlertsService();
		final Alerts alerts = service.get(player);
		alerts.add(Name.of(player));
		alerts.add(Nickname.of(player));
		service.save(alerts);
	}

}
