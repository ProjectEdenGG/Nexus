package gg.projecteden.nexus.features.chat.bridge;

import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.nickname.NicknameService;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gg.projecteden.nexus.features.discord.Discord.discordize;

@NoArgsConstructor
public class IngameBridgeListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChannelChat(PublicChatEvent event) {
		TextChannel discordTextChannel = event.getChannel().getDiscordTextChannel();
		if (discordTextChannel == null) return;

		Player player = event.getChatter().getOnlinePlayer();
		DiscordUser user = new DiscordUserService().get(player);
		RoleManager.update(user);

		String message = event.getMessage();
		message = discordize(message);
		message = parseMentions(message);
		Discord.send(user.getBridgeName() + " **>** " + message, discordTextChannel);
	}

	public static String parseMentions(String message) {
		if (message != null && message.contains("@")) {
			Matcher matcher = Pattern.compile("@[A-Za-z0-9_]+").matcher(message);
			while (matcher.find()) {
				String group = matcher.group();
				String search = group.replace("@", "");
				OfflinePlayer player = Bukkit.getOfflinePlayer(search);
				DiscordUser mentioned = new DiscordUserService().get(player);
				if (mentioned.getUserId() != null) {
					message = message.replace(group, "<@" + mentioned.getUserId() + ">");
					continue;
				}

				Nickname fromNickname = new NicknameService().getFromNickname(search);
				if (fromNickname != null) {
					mentioned = new DiscordUserService().get(fromNickname.getOfflinePlayer());
					if (mentioned.getUserId() != null) {
						message = message.replace(group, "<@" + mentioned.getUserId() + ">");
						continue;
					}
				}
			}
		}
		return message;
	}

}
