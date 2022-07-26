package gg.projecteden.nexus.features.store.perks.joinquit;

import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.Chat.Broadcast.BroadcastBuilder;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.chat.bridge.RoleManager;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.hooks.vanish.VanishHook.VanishStateChangeEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
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
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static gg.projecteden.nexus.features.discord.Discord.discordize;

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
		YamlConfiguration config = IOUtils.getNexusConfig("jq.yml");
		if (config.isConfigurationSection("messages")) {
			joinMessages = config.getConfigurationSection("messages").getStringList("join");
			quitMessages = config.getConfigurationSection("messages").getStringList("quit");
		}
	}

	public static void join(Player player) {
		if (isDuplicate(player, "join"))
			return;

		if (PlayerUtils.isVanished(player)) {
			Broadcast.staffIngame().message(formatJoin(player, "[player] has joined while vanished").replaceAll("&[25]", "&7")).hideFromConsole(true).send();
			return;
		}

		String message = "&a[player] &5has joined the server";
		if (player.hasPermission("jq.custom") && joinMessages.size() > 0)
			message = RandomUtils.randomElement(joinMessages);

		final String finalMessage = message;

		if (player.isOnline()) {
			final String ingame = formatJoin(player, finalMessage);
			final Component component = AdventureUtils.fromLegacyAmpersandText(ingame);

			for (Player receiver : OnlinePlayers.getAll()) {
				if (!MuteMenuUser.hasMuted(receiver, MuteMenuItem.JOIN_QUIT))
					receiver.sendMessage(player, component, MessageType.CHAT);
			}

			if (!player.hasPlayedBefore())
				Jingle.FIRST_JOIN.playAll();
			else
				Jingle.JOIN.playAll();

			Tasks.async(() -> {
				DiscordUser user = new DiscordUserService().get(player);
				RoleManager.update(user);

				final String discord = discordize(finalMessage).replaceAll("\\[player]", "**" + Nickname.discordOf(player) + "**");
				Discord.send("<:blue_arrow_right:883811353641517126> " + discord, TextChannel.BRIDGE);
			});
		}
	}

	public static void quit(Player player) {
		quit(player, QuitReason.DISCONNECTED);
	}

	public static void quit(Player player, QuitReason reason) {
		// Denizen Discord: https://discord.com/channels/315163488085475337/315163488085475337/929117355223695360
		if (!player.isOnline())
			return;

		if (isDuplicate(player, "quit"))
			return;

		if (vanished.contains(player)) {
			Broadcast.staffIngame().message(formatQuit(player, "[player] has left while vanished").replaceAll("&[45]", "&7")).hideFromConsole(true).send();
			return;
		}

		String message = "&c[player] &5has left the server";
		if (player.hasPermission("jq.custom") && quitMessages.size() > 0)
			message = RandomUtils.randomElement(quitMessages);

		final String reasonString;
		if (player.getResourcePackStatus() == Status.DECLINED && !new LocalResourcePackUserService().get(player).isEnabled()) {
			reason = QuitReason.KICKED;
			reasonString = "Resource Pack Declined";
		} else
			reasonString = StringUtils.camelCase(reason.name());

		final String finalMessage = message;
		final String ingame = formatQuit(player, finalMessage);
		final Component component = AdventureUtils.fromLegacyAmpersandText(ingame);
		final Component staffComponent = AdventureUtils.fromLegacyAmpersandText(ingame + " (" + reasonString + ")");

		for (Player receiver : OnlinePlayers.getAll()) {
			if (MuteMenuUser.hasMuted(receiver, MuteMenuItem.JOIN_QUIT))
				continue;

			if (reason != QuitReason.DISCONNECTED && Rank.of(receiver).isStaff())
				receiver.sendMessage(player, staffComponent, MessageType.CHAT);
			else
				receiver.sendMessage(player, component, MessageType.CHAT);
		}

		Jingle.QUIT.playAll();

		Tasks.async(() -> {
			DiscordUser user = new DiscordUserService().get(player);
			RoleManager.update(user);

			final String discord = discordize(finalMessage).replaceAll("\\[player]", "**" + Nickname.discordOf(player) + "**");
			Discord.send("<:red_arrow_left:331808021267218432> " + discord, TextChannel.BRIDGE);
		});
	}

	@NotNull
	public static String formatJoin(Player player, String finalMessage) {
		return "&2 &2&m &2&m &2&m &2>&5 " + finalMessage.replaceAll("\\[player]", "&a" + Nickname.of(player) + "&5");
	}

	@NotNull
	public static String formatQuit(Player player, String finalMessage) {
		return "&4 <&4&m &4&m &4&m &5 " + finalMessage.replaceAll("\\[player]", "&c" + Nickname.of(player) + "&5");
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
			Koda.replyDiscord("**Welcome to Project Eden, " + Nickname.discordOf(player) + "!**");
		}

		join(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.quitMessage(null);
		Player player = event.getPlayer();
		quit(player, event.getReason());
	}

	@EventHandler
	public void on(VanishStateChangeEvent event) {
		Consumer<Function<BroadcastBuilder, BroadcastBuilder>> broadcastSelf = builder ->
			builder.apply(Broadcast.staffIngame().hideFromConsole(true).include(event.getUuid())).send();

		Consumer<Function<BroadcastBuilder, BroadcastBuilder>> broadcastOthers = builder ->
			builder.apply(Broadcast.staffIngame().hideFromConsole(true).exclude(event.getUuid())).send();

		Tasks.wait(1, () -> {
			Player player = Bukkit.getPlayer(event.getUuid());
			if (player == null || !player.isOnline())
				return;

			final String presence = "&f" + Presence.of(player).getCharacter() + " ";

			if (event.isVanishing()) {
				broadcastSelf.accept(builder -> builder.message(presence + "&7You vanished"));
				broadcastOthers.accept(builder -> builder.message(presence + "&e" + Nickname.of(event.getUuid()) + " &7vanished"));
			} else {
				broadcastSelf.accept(builder -> builder.message(presence + "&7You unvanished"));
				broadcastOthers.accept(builder -> builder.message(presence + "&e" + Nickname.of(event.getUuid()) + " &7unvanished"));
			}
		});
	}

	// Can't use Utils#isVanished on player in quit event
	private static final Set<Player> vanished = new HashSet<>();

	static {
		Tasks.repeat(2, 2, JoinQuit::updateVanished);
	}

	public static void updateVanished() {
		OnlinePlayers.getAll().forEach(player -> {
			if (PlayerUtils.isVanished(player))
				vanished.add(player);
			else
				vanished.remove(player);
		});
	}

}
