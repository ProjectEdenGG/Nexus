package me.pugabyte.nexus.features.listeners;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.afk.AFK;
import me.pugabyte.nexus.features.scoreboard.ScoreboardLine;
import me.pugabyte.nexus.models.afk.events.AFKEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class Tab implements Listener {

	static {
		Tasks.repeatAsync(Time.TICK, Time.SECOND.x(5), Tab::update);
	}

	public static void update() {
		PlayerUtils.getOnlinePlayers().forEach(Tab::update);
	}

	public static void update(Player player) {
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
		Nerd nerd = Nerd.of(player);
		String name = nerd.getColoredName();
		if (AFK.get(player).isAfk())
			name += " &7&o[AFK]";
		if (nerd.isVanished())
			name += " &7&o[V]";
		return name.trim();
	}

	@EventHandler
	public void onAFKChange(AFKEvent event) {
		update();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		update(event.getPlayer());
	}

	@EventHandler
	public void onVanishToggle(PlayerVanishStateChangeEvent event) {
		update();
	}

}
