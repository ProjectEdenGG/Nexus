package me.pugabyte.bncore.models.chat;

import lombok.Builder;
import lombok.Data;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@Data
@Builder
public class PublicChannel implements Channel {
	private String name;
	private String nickname;
	private ChatColor color;
	private ChatColor messageColor;
	private DiscordId.Channel discordChannel;
	private ChatColor discordColor;
	private boolean isPrivate;
	private boolean local;
	private boolean crossWorld;
	private String permission;

	public ChatColor getDiscordColor() {
		return discordColor == null ? color : discordColor;
	}

	public ChatColor getMessageColor() {
		return messageColor == null ? Channel.super.getMessageColor() : messageColor;
	}

	@Override
	public String getAssignMessage(Chatter chatter) {
		return "Now chatting in " + color + name;
	}

	public Set<Chatter> getRecipients(Chatter chatter) {
		List<Player> recipients = new ArrayList<>();
		if (local)
			recipients.addAll(Utils.getPlayersNear(chatter.getPlayer().getLocation(), Chat.getLocalRadius()));
		else if (crossWorld)
			recipients.addAll(Bukkit.getOnlinePlayers());
		else
			recipients.addAll(Utils.getPlayersInWorld(chatter.getPlayer().getWorld()));

		return recipients.stream()
				.map(player -> (Chatter) new ChatService().get(player))
				.filter(_chatter -> _chatter.hasJoined(this))
				.collect(Collectors.toSet());
	}

	public void broadcast(String message) {
		broadcastIngame(message);
		broadcastDiscord(message);
	}

	public void broadcastIngame(String message) {
		Bukkit.getConsoleSender().sendMessage(stripColor(message));
		Bukkit.getOnlinePlayers().stream()
				.map(player -> (Chatter) new ChatService().get(player))
				.filter(chatter -> chatter.hasJoined(this))
				.forEach(chatter -> chatter.send(message));
	}

	public void broadcastDiscord(String message) {
		if (discordChannel != null)
			Discord.send(message, discordChannel);
	}

	public void broadcast(JsonBuilder builder) {
		broadcastIngame(builder);
		broadcastDiscord(builder);
	}

	public void broadcastIngame(JsonBuilder builder) {
		Bukkit.getConsoleSender().spigot().sendMessage(builder.build());
		Bukkit.getOnlinePlayers().stream()
				.map(player -> (Chatter) new ChatService().get(player))
				.filter(chatter -> chatter.hasJoined(this))
				.forEach(chatter -> chatter.send(builder));
	}

	public void broadcastDiscord(JsonBuilder builder) {
		if (discordChannel != null)
			Discord.send(builder.toString(), discordChannel);
	}

	public String getPermission() {
		if (permission == null)
			return "chat.use." + name.toLowerCase();
		return permission;
	}

}
