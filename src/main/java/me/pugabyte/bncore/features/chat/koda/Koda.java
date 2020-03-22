package me.pugabyte.bncore.features.chat.koda;

import lombok.Getter;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;

public class Koda {
	@Getter
	private static String localFormat = "&e[L] &5&oKodaBear &e&l> &f";
	@Getter
	private static String dmFormat = "&3&l[&bPM&3&l] &eFrom &3KodaBear &b&l> &e";

	public static void say(String message) {
		ChatManager.getChannel("Global").ifPresent(publicChannel -> publicChannel.broadcast(message));
		Discord.send(message, Channel.BRIDGE);
	}

}
