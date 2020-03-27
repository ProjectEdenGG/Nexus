package me.pugabyte.bncore.features.discord;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.Tasks;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class Discord {

	public Discord() {
		for (Bot bot : Bot.values())
			Tasks.async(() -> {
				bot.connect();
				BNCore.log("Successfully connected " + bot.name() + " to Discord");
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

	public static Guild getGuild() {
		return Bot.KODA.jda().getGuildById(DiscordId.Guild.BEAR_NATION.getId());
	}

	public static void addRole(DiscordUser user, DiscordId.Role role) {
		Role roleById = getGuild().getRoleById(role.getId());
		if (roleById == null)
			BNCore.log("Role from " + role.name() + " not found");
		else
			getGuild().addRoleToMember(user.getUserId(), roleById).queue();
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

}
