package me.pugabyte.nexus.features.chat.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.utils.PlayerUtils;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

@Data
@AllArgsConstructor
public class DiscordChatEvent extends ChatEvent {
	private Member member;
	private PublicChannel channel;
	private String message;
	private String permission;

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPermission() {
		return this.permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public Chatter getChatter() {
		if (member != null) {
			DiscordUser user = new DiscordService().getFromUserId(member.getUser().getId());
			if (user != null && !isNullOrEmpty(user.getUuid()))
				return new ChatService().get(PlayerUtils.getPlayer(user.getUuid()));
		}
		return null;
	}

	@Override
	public String getOrigin() {
		if (getChatter() != null)
			return getChatter().getOfflinePlayer().getName();
		return Discord.getName(member);
	}

	@Override
	public Set<Chatter> getRecipients() {
		return Bukkit.getOnlinePlayers().stream()
						.filter(player -> player.hasPermission(permission))
						.map(player -> (Chatter) new ChatService().get(player))
						.collect(Collectors.toSet());
	}

}
