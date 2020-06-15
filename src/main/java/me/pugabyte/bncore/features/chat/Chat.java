package me.pugabyte.bncore.features.chat;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.alerts.AlertsListener;
import me.pugabyte.bncore.features.chat.bridge.IngameBridgeListener;
import me.pugabyte.bncore.features.chat.translator.Translator;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PublicChannel;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Time.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class Chat {

	// TODO:
	//   Discord queue
	//   /bridge command

	public static final String PREFIX = StringUtils.getPrefix("Chat");

	public Chat() {
		new Timer("    Translator", () -> BNCore.registerListener(new Translator()));
		new Timer("    ChatListener", () -> BNCore.registerListener(new ChatListener()));
		new Timer("    IngameBridgeListener", () -> BNCore.registerListener(new IngameBridgeListener()));
		new Timer("    AlertsListener", () -> BNCore.registerListener(new AlertsListener()));
		new Timer("    addChannels", this::addChannels);
	}

	public static void shutdown() {
		new HashMap<>(new ChatService().getCache()).forEach((uuid, chatter) -> new ChatService().saveSync(chatter));
	}

	private void updateChannels() {
		Bukkit.getOnlinePlayers().stream()
				.map(player -> (Chatter) new ChatService().get(player))
				.forEach(Chatter::updateChannels);
	}

	private void addChannels() {
		for (StaticChannel channel : StaticChannel.values())
			ChatManager.addChannel(channel.getChannel());

		ChatManager.setMainChannel(StaticChannel.GLOBAL.getChannel());
	}

	public enum StaticChannel {
		GLOBAL(PublicChannel.builder()
				.name("Global")
				.nickname("G")
				.discordChannel(Channel.BRIDGE)
				.discordColor(ChatColor.DARK_PURPLE)
				.color(ChatColor.DARK_GREEN)
				.local(false)
				.crossWorld(true)
				.build()),
		LOCAL(PublicChannel.builder()
				.name("Local")
				.nickname("L")
				.color(ChatColor.YELLOW)
				.local(true)
				.crossWorld(false)
				.build()),
		STAFF(PublicChannel.builder()
				.name("Staff")
				.nickname("S")
				.rank(Rank.BUILDER)
				.discordChannel(Channel.STAFF_BRIDGE)
				.color(ChatColor.BLACK)
				.censor(false)
				.local(false)
				.crossWorld(true)
				.build()),
		OPERATOR(PublicChannel.builder()
				.name("Operator")
				.nickname("O")
				.rank(Rank.OPERATOR)
				.discordChannel(Channel.STAFF_OPS_BRIDGE)
				.color(ChatColor.DARK_AQUA)
				.censor(false)
				.local(false)
				.crossWorld(true)
				.build()),
		ADMIN(PublicChannel.builder()
				.name("Admin")
				.nickname("A")
				.rank(Rank.ADMIN)
				.discordChannel(Channel.STAFF_ADMINS)
				.color(ChatColor.BLUE)
				.censor(false)
				.local(false)
				.crossWorld(true)
				.build()),
		MINIGAMES(PublicChannel.builder()
				.name("Minigames")
				.nickname("M")
				.color(ChatColor.DARK_AQUA)
				.local(false)
				.crossWorld(false)
				.build()),
		CREATIVE(PublicChannel.builder()
				.name("Creative")
				.nickname("C")
				.color(ChatColor.AQUA)
				.local(false)
				.crossWorld(false)
				.build());

		@Getter
		private final PublicChannel channel;

		StaticChannel(PublicChannel channel) {
			this.channel = channel;
		}
	}

	public static int getLocalRadius() {
		return BNCore.getInstance().getConfig().getInt("localRadius");
	}

	// Broadcasts

	public static void broadcast(String message) {
		broadcast(message, ChatManager.getMainChannel());
	}

	public static void broadcast(String message, StaticChannel channel) {
		broadcast(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcast(String message, String channel) {
		broadcast(message, ChatManager.getChannel(channel));
	}

	public static void broadcast(String message, PublicChannel channel) {
		channel.broadcast(message);
	}

	public static void broadcast(JsonBuilder message) {
		broadcast(message, ChatManager.getMainChannel());
	}

	public static void broadcast(JsonBuilder message, StaticChannel channel) {
		broadcast(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcast(JsonBuilder message, String channel) {
		broadcast(message, ChatManager.getChannel(channel));
	}

	public static void broadcast(JsonBuilder message, PublicChannel channel) {
		channel.broadcast(message);
	}

	public static void broadcastIngame(String message) {
		broadcastIngame(message, ChatManager.getMainChannel());
	}

	public static void broadcastIngame(String message, StaticChannel channel) {
		broadcastIngame(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcastIngame(String message, String channel) {
		broadcastIngame(message, ChatManager.getChannel(channel));
	}

	public static void broadcastIngame(String message, PublicChannel channel) {
		channel.broadcastIngame(message);
	}

	public static void broadcastIngame(JsonBuilder message) {
		broadcastIngame(message, ChatManager.getMainChannel());
	}

	public static void broadcastIngame(JsonBuilder message, StaticChannel channel) {
		broadcastIngame(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcastIngame(JsonBuilder message, String channel) {
		broadcastIngame(message, ChatManager.getChannel(channel));
	}

	public static void broadcastIngame(JsonBuilder message, PublicChannel channel) {
		channel.broadcastIngame(message);
	}

	public static void broadcastDiscord(String message) {
		broadcastDiscord(message, ChatManager.getMainChannel());
	}

	public static void broadcastDiscord(String message, StaticChannel channel) {
		broadcastDiscord(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcastDiscord(String message, String channel) {
		broadcastDiscord(message, ChatManager.getChannel(channel));
	}

	public static void broadcastDiscord(String message, PublicChannel channel) {
		channel.broadcastDiscord(message);
	}

	public static void broadcastDiscord(JsonBuilder message) {
		broadcastDiscord(message, ChatManager.getMainChannel());
	}

	public static void broadcastDiscord(JsonBuilder message, StaticChannel channel) {
		broadcastDiscord(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcastDiscord(JsonBuilder message, String channel) {
		broadcastDiscord(message, ChatManager.getChannel(channel));
	}

	public static void broadcastDiscord(JsonBuilder message, PublicChannel channel) {
		channel.broadcastDiscord(message);
	}

}
