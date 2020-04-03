package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.translator.Translator;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PublicChannel;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Chat {

	// TODO:
	//   Discord queue

	public static final String PREFIX = StringUtils.getPrefix("Chat");

	public Chat() {
		BNCore.getInstance().addConfigDefault("localRadius", 500);
		new Translator();
		BNCore.registerListener(new ChatListener());
//		BNCore.registerListener(new BridgeListener());
//		BNCore.registerListener(new AlertsListener());
		addChannels();
//		updateChannels();
	}

	public static void shutdown() {
		ChatService.getCache().forEach((uuid, chatter) -> new ChatService().saveSync(chatter));
	}

	public static void broadcast(String message) {
		broadcast(message, ChatManager.getMainChannel());
	}

	public static void broadcast(String message, String channel) {
		broadcast(message, ChatManager.getChannel(channel));
	}

	public static void broadcast(String message, PublicChannel channel) {
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

	private void updateChannels() {
		Bukkit.getOnlinePlayers().stream()
				.map(player -> (Chatter) new ChatService().get(player))
				.forEach(Chatter::updateChannels);
	}

	private void addChannels() {
		PublicChannel global = PublicChannel.builder().name("Global").nickname("g").discordChannel(Channel.BRIDGE).discordColor(ChatColor.DARK_PURPLE).color(ChatColor.DARK_GREEN).local(false).crossWorld(true).build();
		PublicChannel local = PublicChannel.builder().name("Local").nickname("l").color(ChatColor.YELLOW).local(true).crossWorld(false).build();
		PublicChannel staff = PublicChannel.builder().name("Staff").nickname("s").discordChannel(Channel.STAFF_BRIDGE).color(ChatColor.BLACK).local(false).crossWorld(true).build();
		PublicChannel operator = PublicChannel.builder().name("Operator").nickname("o").discordChannel(Channel.STAFF_OPS_BRIDGE).color(ChatColor.DARK_AQUA).local(false).crossWorld(true).build();
		PublicChannel admin = PublicChannel.builder().name("Admin").nickname("a").discordChannel(Channel.STAFF_ADMINS).color(ChatColor.BLUE).local(false).crossWorld(true).build();
		PublicChannel minigames = PublicChannel.builder().name("Minigames").nickname("m").color(ChatColor.DARK_AQUA).local(false).crossWorld(false).build();
		PublicChannel creative = PublicChannel.builder().name("Creative").nickname("c").color(ChatColor.AQUA).local(false).crossWorld(false).build();

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

}
