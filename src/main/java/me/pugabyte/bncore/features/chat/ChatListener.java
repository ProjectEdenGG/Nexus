package me.pugabyte.bncore.features.chat;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.alerts.models.DiscordMessageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

	ChatListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onIngameChat(ChannelChatEvent event) {
		event.setMessage(dotCommand(event.getMessage()));
	}

	@EventHandler
	public void onDiscordChat(DiscordMessageEvent event) {
		event.setMessage(dotCommand(event.getMessage()));
	}

	private String dotCommand(String message) {
		Pattern pattern = Pattern.compile("(\\ |^).\\/(\\/|)[a-zA-Z0-9\\-_]+");
		Matcher matcher = pattern.matcher(message);
		while (matcher.find()) {
			String group = matcher.group();
			String replace = group.replace("./", "/");
			message = message.replace(group, replace);
		}
		return message;
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		Chatter chatter = Herochat.getChatterManager().getChatter(player);
		if (chatter == null) return;
		String[] args = event.getMessage().toLowerCase().split(" ");

		if (args.length > 1 && args[0].toLowerCase().matches("/ch|/herochat")) {
			String channel = args[1].toLowerCase();
			String nick = String.valueOf(channel.charAt(0));

			Channel activeChannel = chatter.getActiveChannel();
			if (activeChannel != null)
				if (activeChannel.getNick().equalsIgnoreCase(nick) || activeChannel.getName().equalsIgnoreCase(channel))
					player.sendMessage(ChatColor.RED + "You are already in that channel");
		}
	}
}
