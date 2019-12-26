package me.pugabyte.bncore.features.minigames;

import lombok.Getter;
import me.pugabyte.bncore.features.minigames.commands.MinigamesCommands;
import me.pugabyte.bncore.features.minigames.listeners.MatchListener;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.reflections.Reflections;

import java.util.Set;

public class Minigames {
	public static final String PREFIX = Utils.getPrefix("Minigames");
	@Getter
	private static World gameworld;
	@Getter
	private static Location gamelobby;

	public Minigames() {
		new MinigamesCommands();
		new MatchListener();

		registerConfigurationTypes();

		gameworld = Bukkit.getWorld("gameworld");
		gamelobby = new Location(gameworld, 1861.5, 38.1, 247.5, 0, 0);

		ArenaManager.read();
	}

	private void registerConfigurationTypes() {
		Set<Class<?>> serializables = new Reflections(this.getClass().getPackage().getName()).getTypesAnnotatedWith(SerializableAs.class);
		for (Class clazz : serializables) {
			String alias = ((SerializableAs) clazz.getAnnotation(SerializableAs.class)).value();
			ConfigurationSerialization.registerClass(clazz, alias);
		}
	}

}
