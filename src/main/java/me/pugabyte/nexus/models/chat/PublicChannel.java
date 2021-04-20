package me.pugabyte.nexus.models.chat;

import lombok.Builder;
import lombok.Data;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.models.mutemenu.MuteMenuUser;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Data
@Builder
public class PublicChannel implements Channel {
	private String name;
	private String nickname;
	private ChatColor color;
	private ChatColor messageColor;
	private TextChannel discordTextChannel;
	private ChatColor discordColor;
	@Builder.Default
	private boolean censor = true;
	private boolean isPrivate;
	private boolean local;
	private boolean crossWorld;
	private String permission;
	private Rank rank;
	@Builder.Default
	private boolean persistent = true;

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

	public String getChatterFormat(Chatter chatter) {
		return color + "[" + nickname.toUpperCase() + "] " + Nerd.of(chatter.getOfflinePlayer()).getChatFormat() + " " + color + ChatColor.BOLD + "> " + getMessageColor();
	}

	public Set<Chatter> getRecipients(Chatter chatter) {
		List<Player> recipients = new ArrayList<>();
		if (local)
			recipients.addAll(Bukkit.getOnlinePlayers().stream()
					.filter(player -> player.getWorld().equals(chatter.getPlayer().getWorld()))
					.filter(player -> player.getLocation().distance(chatter.getPlayer().getLocation()) <= Chat.getLocalRadius())
					.collect(Collectors.toList()));
		else if (crossWorld)
			recipients.addAll(Bukkit.getOnlinePlayers());
		else
			recipients.addAll(chatter.getPlayer().getWorld().getPlayers());

		return recipients.stream()
				.map(player -> new ChatService().get(player))
				.filter(_chatter -> _chatter.canJoin(this))
				.filter(_chatter -> _chatter.hasJoined(this))
				.collect(Collectors.toSet());
	}

	public void broadcast(String message) {
		broadcastIngame(message);
		broadcastDiscord(message);
	}

	public void broadcast(String message, MuteMenuItem muteMenuItem) {
		broadcastIngame(message, muteMenuItem);
		broadcastDiscord(message);
	}

	public void broadcast(Component component) {
		broadcastIngame(component);
		broadcastDiscord(component);
	}

	public void broadcast(Component component, MuteMenuItem muteMenuItem) {
		broadcastIngame(component, muteMenuItem);
		broadcastDiscord(component);
	}

	public void broadcast(UUID sender, Component component, MuteMenuItem muteMenuItem) {
		broadcastIngame(sender, component, muteMenuItem);
		broadcastDiscord(component);
	}

	public void broadcast(Identified sender, Component component, MuteMenuItem muteMenuItem) {
		broadcastIngame(sender, component, muteMenuItem);
		broadcastDiscord(component);
	}

	public void broadcast(Identity sender, Component component, MuteMenuItem muteMenuItem) {
		broadcastIngame(sender, component, muteMenuItem);
		broadcastDiscord(component);
	}

	public void broadcast(UUID sender, Component component, MessageType type) {
		broadcastIngame(sender, component, type);
		broadcastDiscord(component);
	}

	public void broadcast(Identified sender, Component component, MessageType type) {
		broadcastIngame(sender, component, type);
		broadcastDiscord(component);
	}

	public void broadcast(Identity sender, Component component, MessageType type) {
		broadcastIngame(sender, component, type);
		broadcastDiscord(component);
	}

	public void broadcast(UUID sender, Component component) {
		broadcastIngame(sender, component);
		broadcastDiscord(component);
	}

	public void broadcast(Identified sender, Component component) {
		broadcastIngame(sender, component);
		broadcastDiscord(component);
	}

	public void broadcast(Identity sender, Component component) {
		broadcastIngame(sender, component);
		broadcastDiscord(component);
	}

	public void broadcast(Component component, MessageType type) {
		broadcastIngame(component, type);
		broadcastDiscord(component);
	}

	public void broadcast(Identity sender, Component component, MessageType type, MuteMenuItem muteMenuItem) {
		broadcastIngame(sender, component, type, muteMenuItem);
		broadcastDiscord(component);
	}

	public void broadcastIngame(String message) {
		broadcastIngame(message, null);
	}

	public void broadcastIngame(String message, MuteMenuItem muteMenuItem) {
		broadcastIngame(AdventureUtils.fromLegacyText(colorize(message)), muteMenuItem);
	}

	public void broadcastIngame(Component component) {
		broadcastIngame(component, (MuteMenuItem) null);
	}

	public void broadcastIngame(Component component, MuteMenuItem muteMenuItem) {
		broadcastIngame(Identity.nil(), component, MessageType.SYSTEM, muteMenuItem);
	}

	public void broadcastIngame(Component component, MessageType type) {
		broadcastIngame(Identity.nil(), component, type, null);
	}

	public void broadcastIngame(UUID sender, Component component, MessageType type, MuteMenuItem muteMenuItem) {
		broadcastIngame(AdventureUtils.identityOf(sender), component, type, muteMenuItem);
	}

	public void broadcastIngame(Identified sender, Component component, MessageType type, MuteMenuItem muteMenuItem) {
		broadcastIngame(sender.identity(), component, type, muteMenuItem);
	}

	public void broadcastIngame(Identity sender, Component component, MessageType type, MuteMenuItem muteMenuItem) {
		Bukkit.getConsoleSender().sendMessage(AdventureUtils.stripColor(component));
		Bukkit.getOnlinePlayers().stream()
				.map(player -> (Chatter) new ChatService().get(player))
				.filter(chatter -> chatter.hasJoined(this) && !MuteMenuUser.hasMuted(chatter.getOfflinePlayer(), muteMenuItem))
				.forEach(chatter -> chatter.send(sender, component, type));
	}

	public void broadcastIngame(UUID sender, Component component, MessageType type) {
		broadcastIngame(AdventureUtils.identityOf(sender), component, type, null);
	}

	public void broadcastIngame(Identified sender, Component component, MessageType type) {
		broadcastIngame(sender.identity(), component, type, null);
	}

	public void broadcastIngame(Identity sender, Component component, MessageType type) {
		broadcastIngame(sender, component, type, null);
	}

	public void broadcastIngame(UUID sender, Component component) {
		broadcastIngame(AdventureUtils.identityOf(sender), component, MessageType.SYSTEM, null);
	}

	public void broadcastIngame(Identified sender, Component component) {
		broadcastIngame(sender.identity(), component, MessageType.SYSTEM, null);
	}

	public void broadcastIngame(Identity sender, Component component) {
		broadcastIngame(sender, component, MessageType.SYSTEM, null);
	}

	public void broadcastIngame(UUID sender, Component component, MuteMenuItem muteMenuItem) {
		broadcastIngame(AdventureUtils.identityOf(sender), component, MessageType.SYSTEM, muteMenuItem);
	}

	public void broadcastIngame(Identified sender, Component component, MuteMenuItem muteMenuItem) {
		broadcastIngame(sender.identity(), component, MessageType.SYSTEM, muteMenuItem);
	}

	public void broadcastIngame(Identity sender, Component component, MuteMenuItem muteMenuItem) {
		broadcastIngame(sender, component, MessageType.SYSTEM, muteMenuItem);
	}

	public void broadcastDiscord(String message) {
		if (discordTextChannel != null)
			Discord.send(message, discordTextChannel);
	}

	public void broadcastDiscord(Component component) {
		broadcastDiscord(AdventureUtils.asPlainText(component));
	}

	public void broadcast(JsonBuilder builder) {
		broadcastIngame(builder, null);
		broadcastDiscord(builder);
	}

	public void broadcast(JsonBuilder message, MuteMenuItem muteMenuItem) {
		broadcastIngame(message, muteMenuItem);
		broadcastDiscord(message);
	}

	public void broadcastIngame(JsonBuilder builder) {
		broadcastIngame(builder, null);
	}

	public void broadcastIngame(JsonBuilder builder, MuteMenuItem muteMenuItem) {
		broadcastIngame(AdventureUtils.fromJson(builder), muteMenuItem);
	}

	public void broadcastIngame(Chatter chatter, JsonBuilder builder) {
		Bukkit.getConsoleSender().sendMessage(builder.build());
		getRecipients(chatter).forEach(_chatter -> _chatter.send(builder));
	}

	public void broadcastDiscord(JsonBuilder builder) {
		if (discordTextChannel != null)
			Discord.send(builder.toString(), discordTextChannel);
	}

	public String getPermission() {
		if (permission == null)
			return "chat.use." + name.toLowerCase();
		return permission;
	}

}
