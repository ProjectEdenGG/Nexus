package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.PrivateChannel;
import me.pugabyte.bncore.features.chat.models.PublicChannel;
import me.pugabyte.bncore.features.chat.models.events.ChannelChatEvent;
import me.pugabyte.bncore.features.chat.models.events.PrivateChatEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Set;

import static me.pugabyte.bncore.features.chat.ChatManager.getChatter;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

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
	public void onChannelChat(ChannelChatEvent event) {
		if (!event.wasSeen())
			Tasks.wait(1, () -> event.getChatter().send("&eNo one can hear you! Type &c/ch g &eto talk globally"));

		if (!event.getChatter().getPlayer().hasPermission("group.admin"))
			event.setMessage(stripColor(event.getMessage()));

		JsonBuilder json = new JsonBuilder()
				.next(event.getChannel().getColor() + "[" + event.getChannel().getNickname() + "]")
				.next(new Nerd(event.getChatter().getPlayer()).getChatFormat())
				.next(" " + event.getChannel().getColor() + ChatColor.BOLD + "> ")
				.next(event.getMessage());

		event.getRecipients().forEach(recipient -> recipient.send(json));
	}

	@EventHandler
	public void onPrivateChat(PrivateChatEvent event) {
		if (!event.getChatter().getPlayer().hasPermission("group.admin"))
			event.setMessage(stripColor(event.getMessage()));

		Set<String> othersNames = event.getChannel().getOthersNames(event.getChatter());
		JsonBuilder to = new JsonBuilder()
				.next("&3&l[&bPM&3&l] &eTo &3")
				.next(String.join(", ", othersNames))
				.next(" &b&l> &e")
				.next(event.getMessage());

		JsonBuilder from = new JsonBuilder()
				.next("&3&l[&bPM&3&l] &eFrom &3")
				.next(event.getChatter().getPlayer().getName())
				.next(" &b&l> &e")
				.next(event.getMessage());

		event.getRecipients().forEach(recipient -> {
			if (recipient.equals(event.getChatter()))
				recipient.send(to);
			else
				recipient.send(from);
		});
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Chatter chatter = getChatter(event.getPlayer());

		if (chatter.getActiveChannel() instanceof PublicChannel)
			if (!((PublicChannel) chatter.getActiveChannel()).getWorlds().contains(event.getPlayer().getWorld()))
				chatter.setActiveChannel(ChatManager.getMainChannel());

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
