package me.pugabyte.bncore.features.minigames;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.lucko.helper.Services;
import me.lucko.helper.scoreboard.PacketScoreboard;
import me.lucko.helper.scoreboard.PacketScoreboardProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.lobby.ActionBar;
import me.pugabyte.bncore.features.minigames.lobby.Basketball;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.menus.MinigamesMenus;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.models.geoip.GeoIPService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Minigames {
	public static final String PREFIX = StringUtils.getPrefix("Minigames");
	@Getter
	private static World world = Bukkit.getWorld("gameworld");
	@Getter
	private static Location lobby = new Location(world, 1861.5, 38.1, 247.5, 0, 0);
	@Getter
	private static WorldGuardUtils worldGuardUtils = new WorldGuardUtils(world);
	@Getter
	private static WorldEditUtils worldEditUtils = new WorldEditUtils(world);
	@Getter
	private static ProtectedRegion lobbyRegion = worldGuardUtils.getProtectedRegion("minigamelobby");
	@Getter
	public static MinigamesMenus menus = new MinigamesMenus();
	@Getter
	public static PacketScoreboard scoreboard = Services.load(PacketScoreboardProvider.class).getScoreboard();

	public Minigames() {
		registerSerializables();
		ArenaManager.read();
		registerListeners();
		Tasks.repeat(Time.SECOND.x(5), 10, MatchManager::janitor);

		new ActionBar();
		new Basketball();
	}

	public static void shutdown() {
		new ArrayList<>(MatchManager.getAll()).forEach(Match::end);
		ArenaManager.write();
	}

	public static List<Player> getPlayers() {
		return Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld() == world).collect(Collectors.toList());
	}

	public static List<Minigamer> getMinigamers() {
		return getPlayers().stream().map(PlayerManager::get).collect(Collectors.toList());
	}

	public static List<Minigamer> getActiveMinigamers() {
		return getPlayers().stream().map(PlayerManager::get).filter(minigamer -> minigamer.getMatch() != null).collect(Collectors.toList());
	}

	public static void broadcast(String announcement) {
		Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.getWorld().equals(getWorld()))
				.forEach(player -> player.sendMessage(Minigames.PREFIX + colorize(announcement)));

		// TODO: If arena is public, announce to discord and whole server
	}

	public static LocalDateTime getNextMGN() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime next;
		if (now.getDayOfWeek().equals(DayOfWeek.SATURDAY) && now.getHour() <= 18)
			next = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
		else
			next = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

		return next.withHour(16).withMinute(0).withSecond(0).withNano(0);
	}

	public static ZonedDateTime getNextMGNFor(OfflinePlayer player) {
		ZonedDateTime nextMGN = getNextMGN().atZone(ZoneId.systemDefault());
		GeoIP geoIp = new GeoIPService().get(player);
		if (geoIp != null && geoIp.getTimezone() != null)
			return nextMGN.withZoneSameInstant(ZoneId.of(geoIp.getTimezone().getId()));
		return nextMGN;
	}

	// Registration

	private String getPath() {
		return this.getClass().getPackage().getName();
	}

	private void registerListeners() {
		for (Class<? extends Listener> clazz : new Reflections(getPath() + ".listeners").getSubTypesOf(Listener.class)) {
			try {
				BNCore.registerListener(clazz.newInstance());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void registerSerializables() {
		new Reflections(getPath()).getTypesAnnotatedWith(SerializableAs.class).forEach(clazz -> {
			String alias = clazz.getAnnotation(SerializableAs.class).value();
			ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) clazz, alias);
		});
	}

}
