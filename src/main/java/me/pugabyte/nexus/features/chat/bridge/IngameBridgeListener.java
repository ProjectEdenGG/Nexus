package me.pugabyte.nexus.features.chat.bridge;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.events.PublicChatEvent;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.pugabyte.nexus.features.discord.Discord.discordize;

@NoArgsConstructor
public class IngameBridgeListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChannelChat(PublicChatEvent event) {
		DiscordId.Channel discordChannel = event.getChannel().getDiscordChannel();
		if (discordChannel == null) return;

		Player player = event.getChatter().getPlayer();
		DiscordUser user = new DiscordService().get(player);
		RoleManager.update(user);

		String message = event.getMessage();
		message = discordize(message);
		message = parseMentions(message);
		Discord.send(user.getBridgeName() + " **>** " + message, discordChannel);
	}

	public static String parseMentions(String message) {
		if (message != null && message.contains("@")) {
			Matcher matcher = Pattern.compile("@[A-Za-z0-9_]+").matcher(message);
			while (matcher.find()) {
				String group = matcher.group();
				OfflinePlayer player = Bukkit.getOfflinePlayer(group.replace("@", ""));
				if (player.hasPlayedBefore()) {
					DiscordUser mentioned = new DiscordService().get(player);
					if (mentioned.getUserId() != null)
						message = message.replace(group, "<@" + mentioned.getUserId() + ">");
				}
			}
		}
		return message;
	}

}