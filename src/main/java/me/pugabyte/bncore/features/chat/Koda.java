package me.pugabyte.bncore.features.chat;

import lombok.Getter;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.utils.Tasks;

public class Koda {
	private static String nameFormat = "&5&oKodaBear";
	@Getter
	private static String globalFormat = "&2[G] " + nameFormat + " &2&l> &f";
	@Getter
	private static String localFormat = "&e[L] " + nameFormat + " &e&l> &f";
	@Getter
	private static String dmFormat = "&3&l[&bPM&3&l] &eFrom &3KodaBear &b&l> &e";

	public static void reply(String message) {
		Tasks.wait(10, () -> say(message));
	}

	public static void replyIngame(String message) {
		Tasks.wait(10, () -> sayIngame(message));
	}

	public static void replyDiscord(String message) {
		Tasks.wait(10, () -> sayDiscord(message));
	}

	public static void say(String message) {
		sayIngame(message);
		sayDiscord(message);
	}

	public static void sayIngame(String message) {
		Chat.broadcastIngame(globalFormat + message);
	}

	public static void sayDiscord(String message) {
		Chat.broadcastDiscord("<@&&f" + Role.KODA.getId() + "> **>** " + message);
	}

	public static void announce(String message) {
		Discord.send(message, Channel.ANNOUNCEMENTS);
	}

}
