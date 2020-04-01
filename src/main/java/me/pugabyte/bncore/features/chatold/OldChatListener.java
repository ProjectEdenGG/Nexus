package me.pugabyte.bncore.features.chatold;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Censor;
import me.pugabyte.bncore.features.chat.models.events.DiscordChatEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class OldChatListener implements Listener {

	OldChatListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onIngameChat(ChannelChatEvent event) {
		event.setMessage(Censor.dotCommand(event.getMessage()));
	}

	@EventHandler
	public void onDiscordChat(DiscordChatEvent event) {
		event.setMessage(Censor.dotCommand(event.getMessage()));
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
