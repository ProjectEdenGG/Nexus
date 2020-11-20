package me.pugabyte.nexus.models;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public abstract class DatabaseService {

	public Class<? extends PlayerOwnedObject> getPlayerClass() {
		PlayerClass annotation = getClass().getAnnotation(PlayerClass.class);
		return annotation == null ? null : annotation.value();
	}

	public <T> T get(UUID uuid) {
		throw new UnsupportedOperationException();
	}

	public <T> T get(Player player) {
		return (T) get(player.getUniqueId());
	}

	public <T> T get(OfflinePlayer player) {
		return (T) get(player.getUniqueId());
	}

	public <T> T get(Nerd nerd) {
		return (T) get(nerd.getOfflinePlayer());
	}

	abstract public <T> List<T> getAll();

	public <T> void save(T object) {
		if (Bukkit.getServer().isPrimaryThread())
			Tasks.async(() -> saveSync(object));
		else
			saveSync(object);
	}

	abstract public <T> void saveSync(T object);

	public <T> void delete(T object) {
		if (Bukkit.getServer().isPrimaryThread())
			Tasks.async(() -> deleteSync(object));
		else
			deleteSync(object);
	}

	abstract public <T> void deleteSync(T object);

	public void deleteAll() {
		if (Bukkit.getServer().isPrimaryThread())
			Tasks.async(this::deleteAllSync);
		else
			deleteAllSync();
	}

	abstract public void deleteAllSync();

}
