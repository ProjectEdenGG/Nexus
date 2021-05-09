package me.pugabyte.nexus.features.chat;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.events.ChatEvent;
import me.pugabyte.nexus.features.chat.events.DiscordChatEvent;
import me.pugabyte.nexus.features.chat.events.PublicChatEvent;
import me.pugabyte.nexus.features.justice.Justice;
import me.pugabyte.nexus.framework.commands.Commands;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.regex.Pattern;

import static me.pugabyte.nexus.utils.PlayerUtils.runCommand;
import static me.pugabyte.nexus.utils.StringUtils.right;

@NoArgsConstructor
public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Nerd nerd = Nerd.of(event.getPlayer());
		if (nerd.getRank().gt(Rank.GUEST) || nerd.hasMoved()) {
			Chatter chatter = new ChatService().get(event.getPlayer());
			Tasks.sync(() -> {
				// Prevents "t/command"
				if (Pattern.compile("^[tT]" + Commands.getPattern() + ".*").matcher(event.getMessage()).matches())
					runCommand(event.getPlayer(), right(event.getMessage(), event.getMessage().length() - 2));
				else
					chatter.say(event.getMessage());
			});
		} else {
			nerd.send("&cYou must move before you can speak in chat");
			String message = "&e" + nerd.getNickname() + " &ctried to speak before moving: &7" + event.getMessage();
			Chat.broadcastIngame(Justice.PREFIX + message);
			Chat.broadcastDiscord(Justice.DISCORD_PREFIX + message);
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChat(ChatEvent event) {
		Censor.process(event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPublicChat(PublicChatEvent event) {
		Koda.process(event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDiscordChat(DiscordChatEvent event) {
		Koda.process(event);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Chatter chatter = new ChatService().get(event.getPlayer());
		chatter.updateChannels();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Chatter chatter = new ChatService().get(event.getPlayer());
		if (chatter.getActiveChannel() == null)
			chatter.setActiveChannel(ChatManager.getMainChannel());
		chatter.updateChannels();
	}
}
