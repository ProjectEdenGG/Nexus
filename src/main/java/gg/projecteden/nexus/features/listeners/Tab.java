package gg.projecteden.nexus.features.listeners;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateStartEvent;
import gg.projecteden.nexus.features.resourcepack.models.files.FontFile.CustomCharacter;
import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.models.afk.AFKUserService;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.dnd.DNDUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
		player.setPlayerListHeader(StringUtils.colorize(getHeader(player)));
		player.setPlayerListFooter(StringUtils.colorize(getFooter(player)));
		player.setPlayerListName(StringUtils.colorize(getFormat(player)));
	}

	public static String getHeader(Player player) {
		StringBuilder eventLines = new StringBuilder();
		for (EdenEvent event : EdenEvent.getActiveEvents(player)) {
			final String tabLine = event.getTabLine();
			if (tabLine == null)
				continue;

			if (eventLines.isEmpty())
				eventLines.append(System.lineSeparator());

			final String spacer = "&f ".repeat(3);
			eventLines.append(spacer).append(tabLine).append(spacer).append(System.lineSeparator());
		}

		return "%s%n%n%n%n%n%n%n%s%n%s".formatted("貔", ScoreboardLine.ONLINE.render(player), eventLines.toString());
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
		return String.format(" &f%s %s %s ", WorldGroup.of(player).getIcon(), Presence.of(player).ingame(), name);
	}

	public static final List<Presence> PRESENCES = new ArrayList<>();

	@Data
	public static class Presence {
		private final String id;
		private final String name;
		private final String character;
		private final String discordId;
		private final String modelId;

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

		public static final Presence OFFLINE = new Presence("presence_offline", "&#747f8dOffline", "汉", "1007078277921394720", ItemModelType.PRESENCE_OFFLINE.getModel());
		public static final Presence ACTIVE = new Presence("presence_active", "&#3ba55dActive", "", "896466289508356106", ItemModelType.PRESENCE_ACTIVE.getModel());

		public static Presence of(OfflinePlayer player) {
			return of(player, null);
		}

		public static Presence of(OfflinePlayer player, Player viewer) {
			if (player == null || !player.isOnline() || (viewer != null && !PlayerUtils.canSee(viewer, player)))
				return OFFLINE;

			presences:
			for (Presence presence : PRESENCES) {
				for (Modifier modifier : Modifier.values())
					if (!presence.applies(modifier, player.getPlayer()))
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

			private final Predicate<Player> predicate;

			public boolean applies(Player player) {
				return predicate.test(player);
			}
		}

		public static void loadAll() {
			PRESENCES.clear();
			for (CustomCharacter character : ResourcePack.getFontFile().getProviders()) {
				if (character.getFile() == null)
					continue;

				final String id = character.fileName();
				if (!id.startsWith("presence_"))
					continue;

				if (id.contains("offline"))
					continue;

				final Presence presence = new Presence(id, character.getName(), character.getChars().getFirst(), character.getDiscordId(), character.getModelId());
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
	public void onVanishToggle(VanishToggleEvent event) {
		stateChange(event.getPlayer());
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
