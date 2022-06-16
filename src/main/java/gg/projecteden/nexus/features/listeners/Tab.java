package gg.projecteden.nexus.features.listeners;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateStartEvent;
import gg.projecteden.nexus.features.resourcepack.models.files.FontFile.CustomCharacter;
import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.models.afk.AFKUserService;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.dnd.DNDUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

public class Tab implements Listener {

	private static int taskId;

	@EventHandler
	public void on(ResourcePackUpdateStartEvent event) {
		Tasks.cancel(taskId);
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent event) {
		Presence.loadAll();
		taskId = Tasks.repeatAsync(0, TickTime.SECOND.x(5), Tab::update);
	}

	public static void update() {
		OnlinePlayers.getAll().forEach(Tab::update);
	}

	public static void update(@NotNull Player player) {
		player.setPlayerListHeader(colorize(getHeader(player)));
		player.setPlayerListFooter(colorize(getFooter(player)));
		player.setPlayerListName(colorize(getFormat(player)));
	}

	public static String getHeader(Player player) {
		return "%n%s%n".formatted(ScoreboardLine.ONLINE.render(player));
	}

	public static String getFooter(Player player) {
		final String nl = System.lineSeparator();
		return
			nl + ScoreboardLine.PING.render(player) + "  &8&l|  " + ScoreboardLine.TPS.render(player) +
			nl + ScoreboardLine.CHANNEL.render(player) +
			nl + "" +
			nl + "  " + ScoreboardLine.SERVER_TIME.render(player) + "  " +
			nl + "" +
			nl + "&3Join us on &c/discord" +
			nl;
	}

	public static String getFormat(Player player) {
		String name = Nerd.of(player).getColoredName();
		return String.format(" &f%s %s ", Presence.of(player).ingame(), name);
	}

	public static final List<Presence> PRESENCES = new ArrayList<>();

	@Data
	public static class Presence {
		private final String id;
		private final String character;
		private final String discordId;

		public boolean applies(Modifier modifier) {
			return id.toUpperCase().contains(modifier.name());
		}

		public boolean applies(Modifier modifier, Player player) {
			return applies(modifier) == modifier.applies(player);
		}

		public String ingame() {
			return character;
		}

		public String discord() {
			return String.format("<:%s:%s>", id, discordId);
		}

		public boolean isActive() {
			return id.equals(ACTIVE.getId());
		}

		public static final Presence ACTIVE = new Presence("presence_active", "", "896466289508356106");

		public static Presence of(Player player) {
			presences:
			for (Presence presence : PRESENCES) {
				for (Modifier modifier : Modifier.values())
					if (!presence.applies(modifier, player))
						continue presences;

				return presence;
			}

			Nexus.warn("Could not determine " + Nickname.of(player) + "'s presence");
			if (Nexus.isDebug())
				Thread.dumpStack();
			return ACTIVE;
		}

		@AllArgsConstructor
		public enum Modifier {
			AFK(player -> new AFKUserService().get(player).isAfk()),
			DND(player -> new DNDUserService().get(player).isDnd()),
			LIVE(player -> new BadgeUserService().get(player).owns(Badge.TWITCH) && new SocialMediaUserService().get(player).isStreaming()),
			VANISHED(player -> Nerd.of(player).isVanished()),
			;

			private Predicate<Player> predicate;

			public boolean applies(Player player) {
				return predicate.test(player);
			}
		}

		public static void loadAll() {
			PRESENCES.clear();
			for (CustomCharacter character : ResourcePack.getFontFile().getProviders()) {
				final String id = character.fileName();
				if (!id.startsWith("presence_"))
					continue;

				final Presence presence = new Presence(id, character.getChars().get(0), character.getDiscordId());
				PRESENCES.add(presence);
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tab.update(event.getPlayer());
	}

	@EventHandler
	public void onAFKChange(AFKEvent event) {
		stateChange(event.getUser().getPlayer());
	}

	@EventHandler
	public void onVanishToggle(PlayerVanishStateChangeEvent event) {
		stateChange(Bukkit.getPlayer(event.getUUID()));
	}

	@EventHandler
	public void onPlayerRankChange(PlayerRankChangeEvent event) {
		stateChange(Bukkit.getPlayer(event.getUuid()));
	}

	private void stateChange(Player player) {
		if (player == null)
			return;

		Tab.update(player);
	}

}
