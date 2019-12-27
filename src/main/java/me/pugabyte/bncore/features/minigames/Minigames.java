package me.pugabyte.bncore.features.minigames;

import lombok.Getter;
import me.pugabyte.bncore.features.minigames.listeners.MatchListener;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.reflections.Reflections;

import java.util.Set;

public class Minigames {
	public static final String PREFIX = Utils.getPrefix("Minigames");
	@Getter
	private static World gameworld = Bukkit.getWorld("gameworld");
	@Getter
	private static Location gamelobby = new Location(gameworld, 1861.5, 38.1, 247.5, 0, 0);

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

}
