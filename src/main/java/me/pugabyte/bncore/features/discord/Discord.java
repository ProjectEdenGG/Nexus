package me.pugabyte.bncore.features.discord;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class Discord {
	@Getter
	private static final Map<String, DiscordUser> codes = new HashMap<>();
	@Getter
	private static final String url = "https://discord.gg/bearnation";

	public Discord() {
		Tasks.repeatAsync(0, Time.MINUTE, this::connect);
		Tasks.waitAsync(Time.SECOND.x(2), this::connect);
		BNCore.getCron().schedule("*/5 * * * *", Discord::updateTopics);
	}

	public void connect() {
		for (Bot bot : Bot.values())
			if (bot.jda() == null)
				try {
					bot.connect();
					if (bot.jda() != null)
						BNCore.log("Successfully connected " + bot.name() + " to Discord");
					else
						BNCore.log("Could not connect " + bot.name() + " to Discord");
				} catch (Exception ex) {
					BNCore.severe("An error occurred while trying to connect to Discord");
					ex.printStackTrace();
				}
	}

	public static void shutdown() {
		try {
			for (Bot bot : Bot.values())
				bot.shutdown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getName(Member member) {
		User user = null;
		if (member != null)
			user = member.getUser();
		return getName(member, user);
	}

	public static String getName(Member member, User user) {
		if (member == null || member.getNickname() == null)
			if (user == null)
				return "NULL";
			else
				return user.getName();
		return member.getNickname();
	}

	public static String discordize(String message) {
		if (message != null) {
			message = message.replaceAll("_", "\\_");
			message = message.replaceAll("\\\\", "\\\\\\\\"); // what the fuck
		}
		return message;
	}

	public static Guild getGuild() {
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
		if (targets == null || targets.length == 0)
			 targets = new DiscordId.Channel[]{ Channel.BRIDGE };
		message = stripColor(message);
		for (DiscordId.Channel target : targets) {
			if (target == null || Bot.RELAY.jda() == null)
				continue;
			TextChannel channel = Bot.RELAY.jda().getTextChannelById(target.getId());
			if (channel != null)
				channel.sendMessage(message).queue();
		}
	}

	public static void send(MessageBuilder message, DiscordId.Channel... targets) {
		if (targets == null || targets.length == 0)
			targets = new DiscordId.Channel[]{ Channel.BRIDGE };
		for (DiscordId.Channel target : targets) {
			if (target == null || Bot.RELAY.jda() == null)
				continue;
			TextChannel channel = Bot.RELAY.jda().getTextChannelById(target.getId());
			if (channel != null)
				channel.sendMessage(message.build()).queue();
		}
	}

	public static void koda(String message, DiscordId.Channel... targets) {
		if (targets == null || targets.length == 0)
			targets = new DiscordId.Channel[]{ Channel.BRIDGE };
		message = stripColor(message);
		for (DiscordId.Channel target : targets) {
			if (target == null || Bot.KODA.jda() == null)
				continue;
			TextChannel channel = Bot.KODA.jda().getTextChannelById(target.getId());
			if (channel != null)
				channel.sendMessage(message).queue();
		}
	}

	public static void addRole(String userId, DiscordId.Role role) {
		try {
			Role roleById = getGuild().getRoleById(role.getId());
			if (roleById == null)
				BNCore.log("Role from " + role.name() + " not found");
			else
				getGuild().addRoleToMember(userId, roleById).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void removeRole(String userId, DiscordId.Role role) {
		Role roleById = getGuild().getRoleById(role.getId());
		if (roleById == null)
			BNCore.log("Role from " + role.name() + " not found");
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
				.filter(player -> !Utils.isVanished(player))
				.collect(Collectors.toList());

		return "Online nerds (" + players.size() + "): " + players.stream()
				.map(player -> {
					String name = player.getName();
					if (AFK.get(player).isAfk())
						name = "_[AFK]_ " + name;
					return name;
				})
				.collect(Collectors.joining(", "));
	}

	private static void updateBridgeTopic(String newBridgeTopic) {
		bridgeTopic = newBridgeTopic;
		GuildChannel channel = Discord.getGuild().getGuildChannelById(Channel.BRIDGE.getId());
		if (channel != null)
			channel.getManager().setTopic(bridgeTopic).queue();
	}

	private static String getStaffBridgeTopic() {
		List<Player> players = Bukkit.getOnlinePlayers().stream()
				.filter(player -> new Nerd(player).getRank().isStaff())
				.collect(Collectors.toList());

		return "Online staff (" + players.size() + "): " + players.stream()
				.map(player -> {
					String name = player.getName();
					if (Utils.isVanished(player))
						name = "_[V]_ " + name;
					if (AFK.get(player).isAfk())
						name = "_[AFK]_ " + name;
					return name;
				})
				.collect(Collectors.joining(", "));
	}

	private static void updateStaffBridgeTopic(String newStaffBridgeTopic) {
		staffBridgeTopic = newStaffBridgeTopic;
		GuildChannel channel = Discord.getGuild().getGuildChannelById(Channel.STAFF_BRIDGE.getId());
		if (channel != null)
			channel.getManager().setTopic(staffBridgeTopic).queue();
	}

}
