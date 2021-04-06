package me.pugabyte.nexus.features.discord;

import joptsimple.internal.Strings;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.afk.AFK;
import me.pugabyte.nexus.features.discord.DiscordId.Channel;
import me.pugabyte.nexus.features.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.queup.QueUp;
import me.pugabyte.nexus.models.queup.QueUpService;
import me.pugabyte.nexus.utils.Env;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class Discord extends Feature {
	@Getter
	private static final Map<String, DiscordUser> codes = new HashMap<>();

	@Override
	public void onStart() {
		if (Nexus.getEnv() != Env.PROD)
			return;

		Tasks.repeatAsync(0, Time.MINUTE, this::connect);
		Tasks.waitAsync(Time.SECOND.x(2), this::connect);
		Nexus.getCron().schedule("*/6 * * * *", Discord::updateTopics);
	}

	public void connect() {
		for (Bot bot : Bot.values())
			if (bot.jda() == null && bot.getToken().length() > 0)
				try {
					bot.connect();
				} catch (Exception ex) {
					Nexus.severe("An error occurred while trying to connect to Discord");
					ex.printStackTrace();
				}
	}

	@Override
	public void onStop() {
		try {
			for (Bot bot : Bot.values())
				bot.shutdown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getName(String id) {
		String name = getName(Discord.getGuild().retrieveMemberById(id).complete());
		if (name == null) name = id;
		return name;
	}

	public static String getName(Member member) {
		User user = null;
		if (member != null)
			user = member.getUser();
		return getName(member, user);
	}

	public static String getName(Member member, User user) {
		if (member != null)
			if (member.getNickname() != null)
				return member.getNickname();

		if (user != null)
			return user.getName();

		return null;
	}

	public static String discordize(String message) {
		if (message != null) {
			message = message.replaceAll("\\\\", "\\\\\\\\"); // what the fuck
			message = message.replaceAll("_", "\\\\_");
		}
		return message;
	}

	public static String discordize(Component message) {
		return discordize(PlainComponentSerializer.plain().serialize(message));
	}

	public static Guild getGuild() {
		if (Bot.KODA.jda() == null) return null;
		return Bot.KODA.jda().getGuildById(DiscordId.Guild.BEAR_NATION.getId());
	}

	@Deprecated
	public static void staffAlerts(String message) {
		// send(message, Channel.STAFF_ALERTS);
	}

	public static void log(String message) {
		send(message, Channel.STAFF_BRIDGE, Channel.STAFF_LOG);
	}

	public static void staffBridge(String message) {
		send(message, Channel.STAFF_BRIDGE);
	}

	public static void staffLog(String message) {
		send(message, Channel.STAFF_LOG);
	}

	public static void adminLog(String message) {
		send(message, Channel.ADMIN_LOG);
	}

	public static void send(String message, DiscordId.Channel... targets) {
		send(new MessageBuilder(stripColor(message)), targets);
	}

	public static void send(MessageBuilder message, DiscordId.Channel... targets) {
		send(message, success -> {}, error -> {}, targets);
	}

	public static void send(MessageBuilder message, Consumer<Message> onSuccess, Consumer<Throwable> onError, DiscordId.Channel... targets) {
		send(message, onSuccess, onError, Bot.RELAY, targets);
	}

	public static void koda(String message, DiscordId.Channel... targets) {
		koda(new MessageBuilder(stripColor(message)), targets);
	}
	public static void koda(MessageBuilder message, DiscordId.Channel... targets) {
		koda(message, success -> {}, error -> {}, targets);
	}

	public static void koda(MessageBuilder message, Consumer<Message> onSuccess, Consumer<Throwable> onError, DiscordId.Channel... targets) {
		send(message, onSuccess, onError, Bot.KODA, targets);
	}

	private static void send(MessageBuilder message, Consumer<Message> onSuccess, Consumer<Throwable> onError, Bot bot, DiscordId.Channel... targets) {
		if (targets == null || targets.length == 0)
			targets = new Channel[]{ Channel.BRIDGE };
		for (Channel target : targets) {
			if (target == null || bot.jda() == null)
				continue;
			TextChannel channel = bot.jda().getTextChannelById(target.getId());
			if (channel != null)
				channel.sendMessage(message.build()).queue(onSuccess, onError);
		}
	}

	public static void addRole(String userId, DiscordId.Role role) {
		try {
			Role roleById = getGuild().getRoleById(role.getId());
			if (roleById == null)
				Nexus.log("Role from " + role.name() + " not found");
			else
				getGuild().addRoleToMember(userId, roleById).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void removeRole(String userId, DiscordId.Role role) {
		Role roleById = getGuild().getRoleById(role.getId());
		if (roleById == null)
			Nexus.log("Role from " + role.name() + " not found");
		else
			getGuild().removeRoleFromMember(userId, roleById).queue();
	}

	private static String bridgeTopic = "";
	private static String staffBridgeTopic = "";

	private static void updateTopics() {
		String newBridgeTopic = getBridgeTopic();
		String newStaffBridgeTopic = getStaffBridgeTopic();

		if (!bridgeTopic.equals(newBridgeTopic))
			updateBridgeTopic(newBridgeTopic);
		if (!staffBridgeTopic.equals(newStaffBridgeTopic))
			updateStaffBridgeTopic(newStaffBridgeTopic);
	}

	private static String getBridgeTopic() {
		List<Player> players = Bukkit.getOnlinePlayers().stream()
				.filter(player -> !PlayerUtils.isVanished(player))
				.sorted(Comparator.comparing(Player::getName))
				.collect(Collectors.toList());

		String topic = "Online nerds (" + players.size() + "): " + System.lineSeparator() + players.stream()
				.map(player -> {
					String name = discordize(Nerd.of(player).getNickname());
					if (AFK.get(player).isAfk())
						name += " _[AFK]_";
					return name.trim();
				})
				.collect(Collectors.joining(", " + System.lineSeparator()));

		QueUpService queupService = new QueUpService();
		QueUp queup = queupService.get();
		if (!Strings.isNullOrEmpty(queup.getLastSong()))
			topic += System.lineSeparator() + System.lineSeparator() + "Now playing on " + BNSocialMediaSite.QUEUP.getUrl() + ": " + stripColor(queup.getLastSong());

		return topic;
	}

	private static void updateBridgeTopic(String newBridgeTopic) {
		if (Discord.getGuild() == null) return;
		bridgeTopic = newBridgeTopic;
		GuildChannel channel = Discord.getGuild().getGuildChannelById(Channel.BRIDGE.getId());
		if (channel != null)
			channel.getManager().setTopic(bridgeTopic).queue();
	}

	private static String getStaffBridgeTopic() {
		List<Player> players = Bukkit.getOnlinePlayers().stream()
				.filter(player -> Nerd.of(player).getRank().isStaff())
				.sorted(Comparator.comparing(Player::getName))
				.collect(Collectors.toList());

		return "Online staff (" + players.size() + "): " + System.lineSeparator() + players.stream()
				.map(player -> {
					String name = discordize(Nerd.of(player).getNickname());
					if (PlayerUtils.isVanished(player))
						name += " _[V]_";
					if (AFK.get(player).isAfk())
						name += " _[AFK]_";
					return name.trim();
				})
				.collect(Collectors.joining(", " + System.lineSeparator()));
	}

	private static void updateStaffBridgeTopic(String newStaffBridgeTopic) {
		if (Discord.getGuild() == null) return;
		staffBridgeTopic = newStaffBridgeTopic;
		GuildChannel channel = Discord.getGuild().getGuildChannelById(Channel.STAFF_BRIDGE.getId());
		if (channel != null)
			channel.getManager().setTopic(staffBridgeTopic).queue();
	}

}
