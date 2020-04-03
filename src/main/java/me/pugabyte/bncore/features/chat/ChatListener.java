package me.pugabyte.bncore.features.chat;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.chat.events.ChatEvent;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Chatter chatter = new ChatService().get(event.getPlayer());
		Tasks.sync(() -> chatter.say(event.getMessage()));
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(ChatEvent event) {
		Censor.process(event);
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
