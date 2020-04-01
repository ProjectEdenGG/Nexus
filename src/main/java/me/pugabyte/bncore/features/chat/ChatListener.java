package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.models.events.ChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

	public ChatListener() {
		BNCore.registerListener(this);
	}

//	@EventHandler
//	public void onChat(AsyncPlayerChatEvent event) {
//		Chatter chatter = getChatter(event.getPlayer());
//		Tasks.sync(() -> ChatManager.process(chatter, chatter.getActiveChannel(), event.getMessage()));
//		event.setCancelled(true);
//	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(ChatEvent event) {
		Censor.process(event);
	}

//	@EventHandler
//	public void onWorldChange(PlayerChangedWorldEvent event) {
//		Chatter chatter = getChatter(event.getPlayer());
//		chatter.updateChannels();
//	}
//
//	@EventHandler
//	public void onPlayerJoin(PlayerJoinEvent event) {
//		Chatter chatter = getChatter(event.getPlayer());
//		chatter.setActiveChannel(ChatManager.getMainChannel());
//		chatter.updateChannels();
//	}
//
//	@EventHandler
//	public void onPlayerQuit(PlayerQuitEvent event) {
//		Chatter chatter = getChatter(event.getPlayer());
//		if (chatter.getActiveChannel() instanceof PrivateChannel) {
//			Set<Chatter> recipients = ((PrivateChannel) chatter.getActiveChannel()).getRecipients();
//			new ArrayList<>(recipients).stream().filter(recipient -> !recipient.getPlayer().isOnline()).forEach(recipients::remove);
//			if (recipients.size() < 2)
//				recipients.forEach(recipient -> recipient.setActiveChannel(null));
//		}
//	}
}
