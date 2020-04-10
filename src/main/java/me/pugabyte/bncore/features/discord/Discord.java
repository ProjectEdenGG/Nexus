package me.pugabyte.bncore.features.discord;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.net.ProxySelector;
import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class Discord {
	@Getter
	private static final Map<String, DiscordUser> codes = new HashMap<>();
	@Getter
	private static final String url = "https://discord.bnn.gg";

	public Discord() {
		if (ProxySelector.getDefault() != null)
			ProxySelector.setDefault(ProxySelector.getDefault());
		else
			BNCore.warn("ProxySelector default is null");

		Tasks.repeat(1, Time.MINUTE, this::connect);
	}

	public void connect() {
		for (Bot bot : Bot.values())
			if (bot.jda() == null)
				Tasks.async(() -> {
					try {
						bot.connect();
						if (bot.jda() != null)
							BNCore.log("Successfully connected " + bot.name() + " to Discord");
					} catch (Exception ex) {
						BNCore.severe("An error occurred while trying to connect to Discord");
						ex.printStackTrace();
					}
				});
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
		message = message.replaceAll("_", "\\_");
		message = message.replaceAll("\\\\", "\\\\\\\\"); // what the fuck
		return message;
	}

	public static Guild getGuild() {
		return Bot.KODA.jda().getGuildById(DiscordId.Guild.BEAR_NATION.getId());
	}

	public static void log(String message) {
		send(message, Channel.STAFF_BRIDGE, Channel.STAFF_LOG);
	}

	public static void staffLog(String message) {
		send(message, Channel.STAFF_LOG);
	}

	public static void adminLog(String message) {
		send(message, Channel.ADMIN_LOG);
	}

	public static void send(String message, DiscordId.Channel... targets) {
		message = stripColor(message);
		for (DiscordId.Channel target : targets) {
			if (target == null || Bot.RELAY.jda() == null)
				continue;
			TextChannel channel = Bot.RELAY.jda().getTextChannelById(target.getId());
			if (channel != null)
				channel.sendMessage(message).queue();
		}
	}

	public static void koda(String message, DiscordId.Channel... targets) {
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
		Role roleById = getGuild().getRoleById(role.getId());
		if (roleById == null)
			BNCore.log("Role from " + role.name() + " not found");
		else
			getGuild().addRoleToMember(userId, roleById).queue();
	}

	public static void removeRole(String userId, DiscordId.Role role) {
		Role roleById = getGuild().getRoleById(role.getId());
		if (roleById == null)
			BNCore.log("Role from " + role.name() + " not found");
		else
			getGuild().removeRoleFromMember(userId, roleById).queue();
	}

}
