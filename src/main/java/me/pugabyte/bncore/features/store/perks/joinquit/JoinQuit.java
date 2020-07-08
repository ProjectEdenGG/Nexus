package me.pugabyte.bncore.features.store.perks.joinquit;

import lombok.Getter;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.features.chat.bridge.RoleManager;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.pugabyte.bncore.features.discord.Discord.discordize;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class JoinQuit implements Listener {
	@Getter
	private static List<String> joinMessages = new ArrayList<>();
	@Getter
	private static List<String> quitMessages = new ArrayList<>();

	public JoinQuit() {
		BNCore.registerListener(this);
		reloadConfig();
	}

	@SneakyThrows
	public static void reloadConfig() {
		YamlConfiguration config = BNCore.getConfig("jq.yml");
		if (config.isConfigurationSection("messages")) {
			joinMessages = config.getConfigurationSection("messages").getStringList("join");
			quitMessages = config.getConfigurationSection("messages").getStringList("quit");
		}
	}

	public static void join(Player player) {
		if (isDuplicate(player, "join"))
			return;

		String message = "&a[player] &5has joined the server";
		if (player.hasPermission("jq.custom") && joinMessages.size() > 0)
			message = RandomUtils.randomElement(joinMessages);

		final String finalMessage = message;

		if (player.isOnline()) {
			final String ingame = "&2 &2&m &2&m &2&m &2>&5 " + finalMessage.replaceAll("\\[player]", "&a" + player.getName() + "&5");

			// TODO: mutemenu
			Bukkit.getOnlinePlayers().forEach(_player -> {
				_player.sendMessage(colorize(ingame));

				if (!player.hasPlayedBefore())
					Jingle.FIRST_JOIN.playAll();
				else
					Jingle.JOIN.playAll();
			});

			Tasks.async(() -> {
				DiscordUser user = new DiscordService().get(player);
				RoleManager.update(user);

				final String discord = discordize(finalMessage.replaceAll("\\[player]", "**" + player.getName() + "**"));
				Discord.send(":arrow_right: " + discord, Channel.BRIDGE);
			});
		}
	}

	public static void quit(Player player) {
		if (isDuplicate(player, "quit"))
			return;

		String message = "&c[player] &5has left the server";
		if (player.hasPermission("jq.custom") && quitMessages.size() > 0)
			message = RandomUtils.randomElement(quitMessages);

		final String finalMessage = message;

		final String ingame = "&4 <&4&m &4&m &4&m &5 " + finalMessage.replaceAll("\\[player]", "&c" + player.getName() + "&5");

		// TODO: mutemenu
		Bukkit.getOnlinePlayers().forEach(_player -> {
			_player.sendMessage(colorize(ingame));
			Jingle.QUIT.playAll();
		});

		Tasks.async(() -> {
			DiscordUser user = new DiscordService().get(player);
			RoleManager.update(user);

			final String discord = discordize(finalMessage.replaceAll("\\[player]", "**" + player.getName() + "**"));
			Discord.send("<:red_arrow_left:331808021267218432> " + discord, Channel.BRIDGE);
		});
	}

	public static boolean isDuplicate(Player player, String type) {
		return !new CooldownService().check(player, type, 2);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPlayedBefore()) {
			Koda.replyIngame("&lWelcome to Bear Nation, " + player.getName() + "!");
			Koda.replyDiscord("**Welcome to Bear Nation, " + discordize(player.getName()) + "!**");
		}

		if (!Utils.isVanished(player))
			join(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!vanished.contains(player))
			quit(player);
	}

	// Can't use Utils#isVanished on player in quit event
	private static Set<Player> vanished = new HashSet<>();

	static {
		Tasks.repeat(2, 2, JoinQuit::updateVanished);
	}

	public static void updateVanished() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (Utils.isVanished(player))
				vanished.add(player);
			else
				vanished.remove(player);
		});
	}

}
