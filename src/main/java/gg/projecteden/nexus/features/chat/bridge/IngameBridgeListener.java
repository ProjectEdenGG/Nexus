package gg.projecteden.nexus.features.chat.bridge;

import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.nickname.NicknameService;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class IngameBridgeListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChannelChat(PublicChatEvent event) {
		TextChannel discordTextChannel = event.getChannel().getDiscordTextChannel();
		if (discordTextChannel == null) return;

		Player player = event.getChatter().getOnlinePlayer();
		DiscordUser user = new DiscordUserService().get(player);
		RoleManager.update(user);

		Discord.send(user.getBridgeName() + " **>** " + Discord.discordize(parseMentions(event.getMessage())), discordTextChannel);
	}

	public static String parseMentions(String message) {
		if (message != null && message.contains("@")) {
			Matcher matcher = Pattern.compile("@[\\w]+").matcher(message);
			while (matcher.find()) {
				String group = matcher.group();
				String search = group.replace("@", "");
				Nerd nerd = new NerdService().findExact(search);
				if (nerd != null) {
					DiscordUser mentioned = new DiscordUserService().get(nerd);
					if (mentioned.getUserId() != null) {
						message = message.replace(group, "<@" + mentioned.getUserId() + ">");
					} else {
						Nickname fromNickname = new NicknameService().getFromNickname(search);
						if (fromNickname != null) {
							mentioned = new DiscordUserService().get(fromNickname);
							if (mentioned.getUserId() != null)
								message = message.replace(group, "<@" + mentioned.getUserId() + ">");
						}
					}
				}
			}
		}
		return message;
	}

}
