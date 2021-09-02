package gg.projecteden.nexus.features.listeners;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

public class Tab implements Listener {

	static {
		Tasks.repeatAsync(TickTime.TICK, TickTime.SECOND.x(5), Tab::update);
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
