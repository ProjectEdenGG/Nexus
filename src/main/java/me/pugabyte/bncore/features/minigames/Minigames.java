package me.pugabyte.bncore.features.minigames;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.lucko.helper.Services;
import me.lucko.helper.scoreboard.PacketScoreboard;
import me.lucko.helper.scoreboard.PacketScoreboardProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.lobby.ActionBar;
import me.pugabyte.bncore.features.minigames.lobby.Basketball;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.menus.MinigamesMenus;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.framework.features.Feature;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.util.List;
import java.util.stream.Collectors;

public class Minigames extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("Minigames");
	@Getter
	private static final World world = Bukkit.getWorld("gameworld");
	@Getter
	private static final Location lobby = new Location(world, 1861.5, 38.1, 247.5, 0, 0);
	@Getter
	@Deprecated // Use Match#getWGUtils or Arena#getWGUtils
	private static final WorldGuardUtils worldGuardUtils = new WorldGuardUtils(world);
	@Getter
	@Deprecated // Use Match#getWEUtils or Arena#getWEUtils
	private static final WorldEditUtils worldEditUtils = new WorldEditUtils(world);
	@Getter
	private static final ProtectedRegion lobbyRegion = worldGuardUtils.getProtectedRegion("minigamelobby");
	@Getter
	public static final MinigamesMenus menus = new MinigamesMenus();
	@Getter
	public static final PacketScoreboard scoreboard = Services.load(PacketScoreboardProvider.class).getScoreboard();

	@Override
	public void startup() {
//		registerSerializables();
//		ArenaManager.read();
//		registerListeners();
		Tasks.repeat(Time.SECOND.x(5), 10, MatchManager::janitor);

		new ActionBar();
		new Basketball();
	}

	@Override
	public void shutdown() {
//		new ArrayList<>(MatchManager.getAll()).forEach(Match::end);
//		ArenaManager.write();
	}

	public static boolean isMinigameWorld(World world) {
		return WorldGroup.get(world) == WorldGroup.MINIGAMES;
	}

	public static List<Player> getPlayers() {
		return Bukkit.getOnlinePlayers().stream().filter(player -> isMinigameWorld(player.getWorld())).collect(Collectors.toList());
	}

	public static List<Minigamer> getMinigamers() {
		return getPlayers().stream().map(PlayerManager::get).collect(Collectors.toList());
	}

	public static List<Minigamer> getActiveMinigamers() {
		return getPlayers().stream().map(PlayerManager::get).filter(minigamer -> minigamer.getMatch() != null).collect(Collectors.toList());
	}

	public static void broadcast(String announcement) {
		getPlayers().forEach(player -> Utils.send(player, Minigames.PREFIX + announcement));

		// TODO: If arena is public, announce to discord and whole server
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
