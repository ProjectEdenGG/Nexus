package me.pugabyte.bncore.features.minigames;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.lobby.Basketball;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.menus.MinigamesMenus;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.Utils.colorize;

public class Minigames {
	public static final String PREFIX = Utils.getPrefix("Minigames");
	@Getter
	private static World gameworld = Bukkit.getWorld("gameworld");
	@Getter
	private static Location gamelobby = new Location(gameworld, 1861.5, 38.1, 247.5, 0, 0);
	@Getter
	private static WorldGuardUtils worldGuardUtils = new WorldGuardUtils(gameworld);
	@Getter
	private static WorldEditUtils worldEditUtils = new WorldEditUtils(gameworld);
	@Getter
	public static MinigamesMenus menus = new MinigamesMenus();

	public Minigames() {
		registerSerializables();
		ArenaManager.read();
		registerListeners();
		Tasks.repeat(100, 10, MatchManager::janitor);

		new Basketball();
	}

	public static void shutdown() {
		new ArrayList<>(MatchManager.getAll()).forEach(Match::end);
		ArenaManager.write();
	}

	public static List<Player> getPlayers() {
		return Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld() == gameworld).collect(Collectors.toList());
	}

	public static List<Minigamer> getMinigamers() {
		return getPlayers().stream().map(PlayerManager::get).collect(Collectors.toList());
	}

	public static List<Minigamer> getActiveMinigamers() {
		return getPlayers().stream().map(PlayerManager::get).filter(minigamer -> minigamer.getMatch() != null).collect(Collectors.toList());
	}

	public static void broadcast(String announcement) {
		Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.getWorld().equals(getGameworld()))
				.forEach(player -> player.sendMessage(Minigames.PREFIX + colorize(announcement)));

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
