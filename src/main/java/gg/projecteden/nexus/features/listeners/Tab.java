package gg.projecteden.nexus.features.listeners;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.FontFile.CustomCharacter;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.features.socialmedia.commands.TwitchCommand;
import gg.projecteden.nexus.models.afk.AFKUser;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
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
		return String.format(" %s %s ", Presence.of(player).getCharacter(), name);
	}

	public static final List<Presence> PRESENCES = new ArrayList<>();

	static {
		ResourcePack.getLoader().thenRun(() -> {
			for (CustomCharacter character : ResourcePack.getFontFile().getProviders()) {
				final String file = character.getFile();
				if (!file.contains("presence_"))
					continue;

				final String id = file.split("presence_")[1].replace(".png", "");
				final Presence presence = new Presence(id, character.getChars().get(0));
				PRESENCES.add(presence);
			}
		});
	}

	@Data
	public static class Presence {
		private final String id;
		private final String character;

		public boolean applies(Modifier modifier) {
			return id.toUpperCase().contains(modifier.name());
		}

		public static Presence active() {
			return PRESENCES.stream().filter(presence -> presence.getId().equals("active")).findFirst().orElse(new Presence("active", ""));
		}

		public static Presence of(Player player) {
			presences:
			for (Presence presence : PRESENCES) {
				for (Modifier modifier : Modifier.values()) {
					final boolean presenceAppliesModifier = presence.applies(modifier);
					final boolean modifierAppliesPlayer = modifier.applies(player);
					final boolean matches = presenceAppliesModifier != modifierAppliesPlayer;
					Nexus.debug(presence.getId() + " - " + modifier.name() + " | " +
						"presenceAppliesModifier: " + presenceAppliesModifier + " / " +
						"modifierAppliesPlayer: " + modifierAppliesPlayer + " / " +
						"matches: " + matches);
					if (matches)
						continue presences;
				}

				return presence;
			}

			Nexus.warn("Could not determine " + Nickname.of(player) + "'s presence");
			return active();
		}

		@AllArgsConstructor
		public enum Modifier {
			AFK(AFKUser::isAfk),
			DND(player -> false), // TODO
			LIVE(TwitchCommand::isStreaming),
			VANISHED(PlayerUtils::isVanished),
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
		NameplateUtils.update(player);
	}

	public static class NameplateUtils {

		public static void update(HasUniqueId player) {
			// TODO Player specific reload
			PlayerUtils.runCommandAsConsole("nameplates reload");
		}

		public enum NameplateType {
			ANGRY,
			BANANA,
			BLUE,
			BOMB,
			BOOK,
			CROWN,
			FADED,
			GREEN,
			HAPPY,
			HEART,
			JESTER,
			ORC,
			PURPLE_CROWN,
			RAINBOW,
			SAD,
			YELLOW,
			;

			@Nullable
			private static NameplateType of(String key) {
				try {
					return NameplateType.valueOf(key);
				} catch (IllegalArgumentException ex) {
					return null;
				}
			}

			private static final String EQUIPPED_REGEX = "^nameplates.equipped.*";

			public static NameplateType hasEquippedNameplate(HasUniqueId player) {
				return getEquippedNameplate(player.getUniqueId());
			}

			public static boolean hasEquippedNameplate(UUID uuid) {
				return LuckPermsUtils.getPermissions(uuid).stream()
					.anyMatch(node -> node.getKey().matches(EQUIPPED_REGEX));
			}

			public static NameplateType getEquippedNameplate(HasUniqueId player) {
				return getEquippedNameplate(player.getUniqueId());
			}

			public static NameplateType getEquippedNameplate(UUID uuid) {
				return LuckPermsUtils.getPermissions(uuid).stream()
					.map(Node::getKey)
					.filter(key -> key.matches(EQUIPPED_REGEX))
					.map(NameplateType::of)
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(null);
			}

		}

	}

}
