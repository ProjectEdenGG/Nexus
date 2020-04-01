package me.pugabyte.bncore.features.chat.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.features.chat.models.Channel;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.discord.Discord;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.stream.Collectors;

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
	public String getOrigin() {
		return Discord.getName(member, member == null ? null : member.getUser());
	}

	@Override
	public Set<Chatter> getRecipients() {
		return Bukkit.getOnlinePlayers().stream()
						.filter(player -> player.hasPermission(permission))
						.map(ChatManager::getChatter)
						.collect(Collectors.toSet());
	}

}
