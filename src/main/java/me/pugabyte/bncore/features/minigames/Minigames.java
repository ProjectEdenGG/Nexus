package me.pugabyte.bncore.features.minigames;

import lombok.Getter;
import me.pugabyte.bncore.features.minigames.listeners.MatchListener;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.MinigamesMenus;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.reflections.Reflections;

import java.util.Set;

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
		new MatchListener();
	}

	private void registerSerializables() {
		String path = this.getClass().getPackage().getName();
		Set<Class<?>> serializables = new Reflections(path).getTypesAnnotatedWith(SerializableAs.class);
		serializables.forEach(clazz -> {
			String alias = clazz.getAnnotation(SerializableAs.class).value();
			ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) clazz, alias);
		});
	}

	public static void broadcast(String announcement) {
		Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.getWorld().equals(getGameworld()))
				.forEach(player -> player.sendMessage(Minigames.PREFIX + colorize(announcement)));

		// TODO: If arena is public, announce to discord and whole server
	}

}
