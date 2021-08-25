package gg.projecteden.nexus.features.listeners;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import me.lexikiq.HasUniqueId;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

public class Tab implements Listener {

	static {
		Tasks.repeatAsync(Time.TICK, Time.SECOND.x(5), Tab::update);
	}

	public static void update() {
		PlayerUtils.getOnlinePlayers().forEach(Tab::update);
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
		return addStateTags(player, name).trim();
	}

	public static String addStateTags(Player player, String name) {
		if (AFK.get(player).isAfk())
			name += " &7&o[AFK]";
		if (Nerd.of(player).isVanished())
			name += " &7&o[V]";
		return name;
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

	private void stateChange(Player player) {
		if (player == null)
			return;

		Tab.update(player);
		NameplateType.update(player);
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

		public static void update(Player player) {
			// TODO Player specific reload
			PlayerUtils.runCommandAsConsole("nameplates reload");
		}
	}

}
