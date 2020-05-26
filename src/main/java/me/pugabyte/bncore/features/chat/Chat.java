package me.pugabyte.bncore.features.chat;

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
	//   Censor
	//   Koda stuff
	//   All other discord stuff (/discord link)
	//   Staff alerts
	//   ShowEnchants on bridge
	//   Honeypot announcement

	public static final String PREFIX = StringUtils.getPrefix("Chat");

	public Chat() {
		new Timer("    Translator", () -> BNCore.registerListener(new Translator()));
		new Timer("    ChatListener", () -> BNCore.registerListener(new ChatListener()));
		new Timer("    IngameBridgeListener", () -> BNCore.registerListener(new IngameBridgeListener()));
		new Timer("    AlertsListener", () -> BNCore.registerListener(new AlertsListener()));
		new Timer("    addChannels", this::addChannels);
	}

	public static void shutdown() {
		new HashMap<>(ChatService.getCache()).forEach((uuid, chatter) -> new ChatService().saveSync(chatter));
	}

	private void updateChannels() {
		Bukkit.getOnlinePlayers().stream()
				.map(player -> (Chatter) new ChatService().get(player))
				.forEach(Chatter::updateChannels);
	}

	private void addChannels() {
		PublicChannel global = PublicChannel.builder().name("Global").nickname("G").discordChannel(Channel.BRIDGE).discordColor(ChatColor.DARK_PURPLE).color(ChatColor.DARK_GREEN).local(false).crossWorld(true).build();
		PublicChannel local = PublicChannel.builder().name("Local").nickname("L").color(ChatColor.YELLOW).local(true).crossWorld(false).build();
		PublicChannel staff = PublicChannel.builder().name("Staff").nickname("S").rank(Rank.BUILDER).discordChannel(Channel.STAFF_BRIDGE).color(ChatColor.BLACK).local(false).crossWorld(true).build();
		PublicChannel operator = PublicChannel.builder().name("Operator").nickname("O").rank(Rank.OPERATOR).discordChannel(Channel.STAFF_OPS_BRIDGE).color(ChatColor.DARK_AQUA).local(false).crossWorld(true).build();
		PublicChannel admin = PublicChannel.builder().name("Admin").nickname("A").rank(Rank.ADMIN).discordChannel(Channel.STAFF_ADMINS).color(ChatColor.BLUE).local(false).crossWorld(true).build();
		PublicChannel minigames = PublicChannel.builder().name("Minigames").nickname("M").color(ChatColor.DARK_AQUA).local(false).crossWorld(false).build();
		PublicChannel creative = PublicChannel.builder().name("Creative").nickname("C").color(ChatColor.AQUA).local(false).crossWorld(false).build();

		ChatManager.addChannel(global);
		ChatManager.addChannel(local);
		ChatManager.addChannel(staff);
		ChatManager.addChannel(operator);
		ChatManager.addChannel(admin);
		ChatManager.addChannel(minigames);
		ChatManager.addChannel(creative);

		ChatManager.setMainChannel(global);
	}

	public static int getLocalRadius() {
		return BNCore.getInstance().getConfig().getInt("localRadius");
	}

	// Broadcasts

	public static void broadcast(String message) {
		broadcast(message, ChatManager.getMainChannel());
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

	public static void broadcast(JsonBuilder message, String channel) {
		broadcast(message, ChatManager.getChannel(channel));
	}

	public static void broadcast(JsonBuilder message, PublicChannel channel) {
		channel.broadcast(message);
	}

	public static void broadcastIngame(String message) {
		broadcastIngame(message, ChatManager.getMainChannel());
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

	public static void broadcastIngame(JsonBuilder message, String channel) {
		broadcastIngame(message, ChatManager.getChannel(channel));
	}

	public static void broadcastIngame(JsonBuilder message, PublicChannel channel) {
		channel.broadcastIngame(message);
	}

	public static void broadcastDiscord(String message) {
		broadcastDiscord(message, ChatManager.getMainChannel());
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

	public static void broadcastDiscord(JsonBuilder message, String channel) {
		broadcastDiscord(message, ChatManager.getChannel(channel));
	}

	public static void broadcastDiscord(JsonBuilder message, PublicChannel channel) {
		channel.broadcastDiscord(message);
	}

}
