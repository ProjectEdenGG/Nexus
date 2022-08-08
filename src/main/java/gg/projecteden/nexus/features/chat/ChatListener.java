package gg.projecteden.nexus.features.chat;

import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.features.chat.events.DiscordChatEvent;
import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.regex.Pattern;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.PlayerUtils.runCommand;
import static gg.projecteden.nexus.utils.StringUtils.right;

@NoArgsConstructor
public class ChatListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void on(AsyncChatEvent event) {
		Chatter chatter = new ChatterService().get(event.getPlayer());
		Tasks.sync(() -> {
			// Prevents "t/command"
			final String msg = AdventureUtils.asLegacyText(event.message());
			if (Pattern.compile("^[tT]" + Commands.getPattern() + ".*").matcher(msg).matches())
				runCommand(event.getPlayer(), right(msg, msg.length() - 2));
			else
				chatter.say(msg);
		});
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChat(ChatEvent event) {
		Censor.process(event);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEmptyChat(ChatEvent event) {
		if (!isNullOrEmpty(event.getMessage()))
			return;
		if (event instanceof DiscordChatEvent discordChatEvent && discordChatEvent.hasAttachments())
			return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPublicChat(PublicChatEvent event) {
		Koda.process(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDiscordChat(DiscordChatEvent event) {
		Koda.process(event);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Chatter chatter = new ChatterService().get(event.getPlayer());
		chatter.updateChannels();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Chatter chatter = new ChatterService().get(event.getPlayer());
		if (chatter.getActiveChannel() == null)
			chatter.setActiveChannel(ChatManager.getMainChannel());
		chatter.updateChannels();
	}
}
