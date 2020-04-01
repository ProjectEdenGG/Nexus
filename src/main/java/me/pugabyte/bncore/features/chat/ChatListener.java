package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.PrivateChannel;
import me.pugabyte.bncore.features.chat.models.events.ChatEvent;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Set;

import static me.pugabyte.bncore.features.chat.ChatManager.getChatter;

public class ChatListener implements Listener {

	public ChatListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Chatter chatter = getChatter(event.getPlayer());
		Tasks.sync(() -> ChatManager.process(chatter, chatter.getActiveChannel(), event.getMessage()));
		event.setCancelled(true);
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		Censor.process(event);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Chatter chatter = getChatter(event.getPlayer());
		chatter.updateChannels();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Chatter chatter = getChatter(event.getPlayer());
		chatter.setActiveChannel(ChatManager.getMainChannel());
		chatter.updateChannels();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Chatter chatter = getChatter(event.getPlayer());
		if (chatter.getActiveChannel() instanceof PrivateChannel) {
			Set<Chatter> recipients = ((PrivateChannel) chatter.getActiveChannel()).getRecipients();
			new ArrayList<>(recipients).stream().filter(recipient -> !recipient.getPlayer().isOnline()).forEach(recipients::remove);
			if (recipients.size() < 2)
				recipients.forEach(recipient -> recipient.setActiveChannel(null));
		}
	}
}
