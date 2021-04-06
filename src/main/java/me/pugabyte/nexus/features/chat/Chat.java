package me.pugabyte.nexus.features.chat;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.alerts.AlertsListener;
import me.pugabyte.nexus.features.chat.bridge.IngameBridgeListener;
import me.pugabyte.nexus.features.chat.translator.Translator;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.discord.DiscordId.Channel;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Time.Timer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Chat extends Feature {

	// TODO:
	//   Discord queue
	//   /bridge command

	public static final String PREFIX = StringUtils.getPrefix("Chat");

	@Override
	public void onStart() {
		new Timer("    addChannels", this::addChannels);
		new Timer("    ChatListener", () -> Nexus.registerListener(new ChatListener()));
		new Timer("    IngameBridgeListener", () -> Nexus.registerListener(new IngameBridgeListener()));
		new Timer("    AlertsListener", () -> Nexus.registerListener(new AlertsListener()));
		new Timer("    Translator", () -> Nexus.registerListener(new Translator()));
		new Timer("    updateChannels", this::updateChannels);
	}

	@Override
	public void onStop() {
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
				.discordChannel(Channel.STAFF_OPERATORS)
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
				.crossWorld(true)
				.build()),
		CREATIVE(PublicChannel.builder()
				.name("Creative")
				.nickname("C")
				.color(ChatColor.AQUA)
				.local(false)
				.crossWorld(false)
				.build()),
		SKYBLOCK(PublicChannel.builder()
				.name("Skyblock")
				.nickname("B")
				.color(ChatColor.GOLD)
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
		return Nexus.getInstance().getConfig().getInt("localRadius");
	}

	// Broadcasts

	public static void broadcast(String message) {
		broadcast(message, ChatManager.getMainChannel());
	}

	public static void broadcast(String message, MuteMenuItem muteMenuItem) {
		broadcast(message, ChatManager.getMainChannel(), muteMenuItem);
	}

	public static void broadcast(String message, StaticChannel channel) {
		broadcast(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcast(String message, StaticChannel channel, MuteMenuItem muteMenuItem) {
		broadcast(message, ChatManager.getChannel(channel.name()), muteMenuItem);
	}

	public static void broadcast(String message, String channel) {
		broadcast(message, ChatManager.getChannel(channel));
	}

	public static void broadcast(String message, PublicChannel channel) {
		broadcast(message, channel, null);
	}

	public static void broadcast(String message, PublicChannel channel, MuteMenuItem muteMenuItem) {
		channel.broadcast(message, muteMenuItem);
	}

	public static void broadcast(JsonBuilder message) {
		broadcast(message, ChatManager.getMainChannel());
	}

	public static void broadcast(JsonBuilder message, MuteMenuItem muteMenuItem) {
		broadcast(message, ChatManager.getMainChannel(), muteMenuItem);
	}

	public static void broadcast(JsonBuilder message, StaticChannel channel) {
		broadcast(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcast(JsonBuilder message, StaticChannel channel, MuteMenuItem muteMenuItem) {
		broadcast(message, ChatManager.getChannel(channel.name()), muteMenuItem);
	}

	public static void broadcast(JsonBuilder message, String channel) {
		broadcast(message, ChatManager.getChannel(channel));
	}

	public static void broadcast(JsonBuilder message, PublicChannel channel) {
		broadcast(message, channel, null);
	}

	public static void broadcast(JsonBuilder message, PublicChannel channel, MuteMenuItem muteMenuItem) {
		channel.broadcast(message, muteMenuItem);
	}

	public static void broadcast(Component message) {
		broadcast(message, ChatManager.getMainChannel());
	}

	public static void broadcast(Component message, MuteMenuItem muteMenuItem) {
		broadcast(message, ChatManager.getMainChannel(), muteMenuItem);
	}

	public static void broadcast(Component message, StaticChannel channel) {
		broadcast(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcast(Component message, StaticChannel channel, MuteMenuItem muteMenuItem) {
		broadcast(message, ChatManager.getChannel(channel.name()), muteMenuItem);
	}

	public static void broadcast(Component message, String channel) {
		broadcast(message, ChatManager.getChannel(channel));
	}

	public static void broadcast(Component message, PublicChannel channel) {
		broadcast(message, channel, null);
	}

	public static void broadcast(Component message, PublicChannel channel, MuteMenuItem muteMenuItem) {
		channel.broadcast(message, muteMenuItem);
	}

	public static void broadcastIngame(String message) {
		broadcastIngame(message, ChatManager.getMainChannel());
	}

	public static void broadcastIngame(String message, MuteMenuItem muteMenuItem) {
		broadcastIngame(message, ChatManager.getMainChannel(), muteMenuItem);
	}

	public static void broadcastIngame(String message, StaticChannel channel) {
		broadcastIngame(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcastIngame(String message, StaticChannel channel, MuteMenuItem muteMenuItem) {
		broadcastIngame(message, ChatManager.getChannel(channel.name()), muteMenuItem);
	}

	public static void broadcastIngame(String message, String channel) {
		broadcastIngame(message, ChatManager.getChannel(channel));
	}

	public static void broadcastIngame(String message, PublicChannel channel) {
		broadcastIngame(message, channel, null);
	}

	public static void broadcastIngame(String message, PublicChannel channel, MuteMenuItem muteMenuItem) {
		channel.broadcastIngame(message, muteMenuItem);
	}

	public static void broadcastIngame(JsonBuilder message) {
		broadcastIngame(message, ChatManager.getMainChannel());
	}

	public static void broadcastIngame(JsonBuilder message, MuteMenuItem muteMenuItem) {
		broadcastIngame(message, ChatManager.getMainChannel(), muteMenuItem);
	}

	public static void broadcastIngame(JsonBuilder message, StaticChannel channel) {
		broadcastIngame(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcastIngame(JsonBuilder message, StaticChannel channel, MuteMenuItem muteMenuItem) {
		broadcastIngame(message, ChatManager.getChannel(channel.name()), muteMenuItem);
	}

	public static void broadcastIngame(JsonBuilder message, String channel) {
		broadcastIngame(message, ChatManager.getChannel(channel));
	}

	public static void broadcastIngame(JsonBuilder message, PublicChannel channel) {
		broadcastIngame(message, channel, null);
	}

	public static void broadcastIngame(JsonBuilder message, PublicChannel channel, MuteMenuItem muteMenuItem) {
		channel.broadcastIngame(message, muteMenuItem);
	}

	public static void broadcastIngame(Component message) {
		broadcastIngame(message, ChatManager.getMainChannel());
	}

	public static void broadcastIngame(Component message, MuteMenuItem muteMenuItem) {
		broadcastIngame(message, ChatManager.getMainChannel(), muteMenuItem);
	}

	public static void broadcastIngame(Component message, StaticChannel channel) {
		broadcastIngame(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcastIngame(Component message, StaticChannel channel, MuteMenuItem muteMenuItem) {
		broadcastIngame(message, ChatManager.getChannel(channel.name()), muteMenuItem);
	}

	public static void broadcastIngame(Component message, String channel) {
		broadcastIngame(message, ChatManager.getChannel(channel));
	}

	public static void broadcastIngame(Component message, PublicChannel channel) {
		broadcastIngame(message, channel, null);
	}

	public static void broadcastIngame(Component message, PublicChannel channel, MuteMenuItem muteMenuItem) {
		channel.broadcastIngame(message, muteMenuItem);
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

	public static void broadcastDiscord(Component message) {
		broadcastDiscord(message, ChatManager.getMainChannel());
	}

	public static void broadcastDiscord(Component message, StaticChannel channel) {
		broadcastDiscord(message, ChatManager.getChannel(channel.name()));
	}

	public static void broadcastDiscord(Component message, String channel) {
		broadcastDiscord(message, ChatManager.getChannel(channel));
	}

	public static void broadcastDiscord(Component message, PublicChannel channel) {
		channel.broadcastDiscord(message);
	}

	public static Component stripColor(Component component) {
		component = component.style(Style.empty());
		if (component instanceof TranslatableComponent) {
			TranslatableComponent tComponent = (TranslatableComponent) component;
			component = tComponent.args(stripColor(tComponent.args()));
		}
		return component.children(stripColor(component.children()));
	}

	public static List<Component> stripColor(Collection<Component> components) {
		return components.stream().map(Chat::stripColor).collect(Collectors.toList());
	}

}
