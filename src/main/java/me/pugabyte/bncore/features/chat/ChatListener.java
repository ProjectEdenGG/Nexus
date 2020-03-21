package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.events.ChannelChatEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ChatListener implements Listener {

	public ChatListener() {
		BNCore.registerListener(this);
	}

	public void onChat(AsyncPlayerChatEvent event) {
		Chatter chatter = ChatManager.getChatter(event.getPlayer());
		String message = event.getMessage();

		ChannelChatEvent channelChatEvent = new ChannelChatEvent(chatter, chatter.getActiveChannel(), message);

		Tasks.sync(() -> {
			Utils.callEvent(channelChatEvent);
			if (channelChatEvent.isCancelled()) return;

			ChatManager.handleChat(channelChatEvent);
		});
	}

	public void onWorldChange(PlayerChangedWorldEvent event) {
		Chatter chatter = ChatManager.getChatter(event.getPlayer());

		if (!chatter.getActiveChannel().getWorlds().contains(event.getPlayer().getWorld()))
			chatter.setActiveChannel(ChatManager.getMainChannel());
	}
}
