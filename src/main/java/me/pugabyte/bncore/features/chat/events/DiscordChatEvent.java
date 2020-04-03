package me.pugabyte.bncore.features.chat.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.models.chat.Channel;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

@Data
@AllArgsConstructor
public class DiscordChatEvent extends ChatEvent {
	private Member member;
	private Channel channel;
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
			if (!isNullOrEmpty(user.getUuid()))
				return new ChatService().get(Utils.getPlayer(user.getUuid()));
		}
		return null;
	}

	@Override
	public String getOrigin() {
		if (getChatter() != null)
			return getChatter().getOfflinePlayer().getName();
		return Discord.getName(member, member == null ? null : member.getUser());
	}

	@Override
	public Set<Chatter> getRecipients() {
		return Bukkit.getOnlinePlayers().stream()
						.filter(player -> player.hasPermission(permission))
						.map(player -> (Chatter) new ChatService().get(player))
						.collect(Collectors.toSet());
	}

}
