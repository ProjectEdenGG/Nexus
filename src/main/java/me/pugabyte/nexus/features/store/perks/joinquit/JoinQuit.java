package me.pugabyte.nexus.features.store.perks.joinquit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.features.chat.bridge.RoleManager;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.models.mutemenu.MuteMenuUser;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.SoundUtils.Jingle;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerQuitEvent.QuitReason;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.pugabyte.nexus.features.discord.Discord.discordize;

@NoArgsConstructor
public class JoinQuit extends Feature implements Listener {
	@Getter
	private static List<String> joinMessages = new ArrayList<>();
	@Getter
	private static List<String> quitMessages = new ArrayList<>();

	@Override
	public void onStart() {
		reloadConfig();
	}

	@SneakyThrows
	public static void reloadConfig() {
		YamlConfiguration config = Nexus.getConfig("jq.yml");
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
			final String ingame = "&2 &2&m &2&m &2&m &2>&5 " + finalMessage.replaceAll("\\[player]", "&a" + Nickname.of(player) + "&5");
			final Component component = AdventureUtils.fromLegacyAmpersandText(ingame);

			Bukkit.getOnlinePlayers().forEach(_player -> {
				if (!MuteMenuUser.hasMuted(_player, MuteMenuItem.JOIN_QUIT))
					_player.sendMessage(player, component, MessageType.CHAT);
			});

			if (!player.hasPlayedBefore())
				Jingle.FIRST_JOIN.playAll();
			else
				Jingle.JOIN.playAll();

			Tasks.async(() -> {
				DiscordUser user = new DiscordUserService().get(player);
				RoleManager.update(user);

				final String discord = discordize(finalMessage.replaceAll("\\[player]", "**" + Nickname.of(player) + "**"));
				Discord.send(":arrow_right: " + discord, TextChannel.BRIDGE);
			});
		}
	}

	public static void quit(Player player) {
		quit(player, QuitReason.DISCONNECTED);
	}

	public static void quit(Player player, QuitReason reason) {
		if (isDuplicate(player, "quit"))
			return;

		String message = "&c[player] &5has left the server";
		if (player.hasPermission("jq.custom") && quitMessages.size() > 0)
			message = RandomUtils.randomElement(quitMessages);

		final String finalMessage = message;

		final String ingame = "&4 <&4&m &4&m &4&m &5 " + finalMessage.replaceAll("\\[player]", "&c" + Nickname.of(player) + "&5");
		final Component component = AdventureUtils.fromLegacyAmpersandText(ingame);
		final Component staffComponent = AdventureUtils.fromLegacyAmpersandText(ingame + " (" + StringUtils.camelCase(reason.name()) + ")");

		Bukkit.getOnlinePlayers().forEach(_player -> {
			if (!MuteMenuUser.hasMuted(_player, MuteMenuItem.JOIN_QUIT)) {
				if (reason != QuitReason.DISCONNECTED && PlayerUtils.isStaffGroup(_player))
					_player.sendMessage(player, staffComponent, MessageType.CHAT);
				else
					_player.sendMessage(player, component, MessageType.CHAT);
			}
		});

		Jingle.QUIT.playAll();

		Tasks.async(() -> {
			DiscordUser user = new DiscordUserService().get(player);
			RoleManager.update(user);

			final String discord = discordize(finalMessage.replaceAll("\\[player]", "**" + Nickname.of(player) + "**"));
			Discord.send("<:red_arrow_left:331808021267218432> " + discord, TextChannel.BRIDGE);
		});
	}

	public static boolean isDuplicate(Player player, String type) {
		return !new CooldownService().check(player, type, 2);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.joinMessage(null);
		Player player = event.getPlayer();
		if (!player.hasPlayedBefore()) {
			Koda.replyIngame("&lWelcome to Project Eden, " + Nickname.of(player) + "!");
			Koda.replyDiscord("**Welcome to Project Eden, " + discordize(Nickname.of(player)) + "!**");
		}

		if (!PlayerUtils.isVanished(player))
			join(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.quitMessage(null);
		Player player = event.getPlayer();
		if (!vanished.contains(player))
			quit(player, event.getReason());
	}

	// Can't use Utils#isVanished on player in quit event
	private static Set<Player> vanished = new HashSet<>();

	static {
		Tasks.repeat(2, 2, JoinQuit::updateVanished);
	}

	public static void updateVanished() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (PlayerUtils.isVanished(player))
				vanished.add(player);
			else
				vanished.remove(player);
		});
	}

}
