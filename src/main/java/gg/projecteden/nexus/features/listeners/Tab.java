package gg.projecteden.nexus.features.listeners;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.FontFile.CustomCharacter;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.models.afk.AFKUserService;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.dnd.DNDUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.lexikiq.HasUniqueId;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

public class Tab implements Listener {

	static {
		Tasks.repeatAsync(TickTime.TICK, TickTime.SECOND.x(5), Tab::update);
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
		return System.lineSeparator() + ScoreboardLine.ONLINE.render(player) + System.lineSeparator();
	}

	public static String getFooter(Player player) {
		return System.lineSeparator() +
				"  " + ScoreboardLine.PING.render(player) + "  &8&l|  " + ScoreboardLine.TPS.render(player) + "  " +
				System.lineSeparator() +
				ScoreboardLine.CHANNEL.render(player) +
				System.lineSeparator() +
				"" +
				System.lineSeparator() +
				"&3Join us on &c/discord" +
				System.lineSeparator();
	}

	public static String getFormat(Player player) {
		String name = Nerd.of(player).getColoredName();
		return String.format(" &f%s %s ", Presence.of(player).getCharacter(), name);
	}

	public static final List<Presence> PRESENCES = new ArrayList<>();

	static {
		ResourcePack.getLoader().thenRun(() -> {
			for (CustomCharacter character : ResourcePack.getFontFile().getProviders()) {
				final String id = character.fileName();
				if (!id.startsWith("presence_"))
					continue;

				final Presence presence = new Presence(id, character.getChars().get(0), character.getDiscordId());
				PRESENCES.add(presence);
			}

			Tasks.repeatAsync(0, TickTime.SECOND.x(5), Tab::update);
		});
	}

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

		public String discord() {
			return String.format("<:%s:%s>", id, discordId);
		}

		public boolean isActive() {
			return id.equals(ACTIVE.getId());
		}

		public static final Presence ACTIVE = new Presence("presence_active", "", "892994697600040990");

		public static Presence of(Player player) {
			presences:
			for (Presence presence : PRESENCES) {
				for (Modifier modifier : Modifier.values())
					if (!presence.applies(modifier, player))
						continue presences;

				return presence;
			}

			Nexus.warn("Could not determine " + Nickname.of(player) + "'s presence");
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
		Nameplates.get().getFakeEntityManager().updateFakeEntityAroundPlayer(player);
	}

}
