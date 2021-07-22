package gg.projecteden.nexus.models;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class DatabaseService {

	public Class<? extends gg.projecteden.interfaces.PlayerOwnedObject> getPlayerClass() {
		PlayerClass annotation = getClass().getAnnotation(PlayerClass.class);
		return annotation == null ? null : annotation.value();
	}

	public <T> T get(UUID uuid) {
		throw new UnsupportedOperationException();
	}

	public <T> T get(Player player) {
		return get(player.getUniqueId());
	}

	public <T> T get(OfflinePlayer player) {
		return get(player.getUniqueId());
	}

	public <T> T get(Nerd nerd) {
		return get(nerd.getOfflinePlayer());
	}

	public <T> T get(PlayerOwnedObject player) {
		return get(player.getUuid());
	}

	abstract public <T> List<T> getAll();

	public <T> void save(T object) {
		checkType(object);
		if (Bukkit.isPrimaryThread())
			Tasks.async(() -> saveSync(object));
		else
			saveSync(object);
	}

	private <T> void checkType(T object) {
		if (getPlayerClass() == null) return;
		if (!object.getClass().isAssignableFrom(getPlayerClass()))
			throw new InvalidInputException(this.getClass().getSimpleName() + " received wrong class type, expected "
					+ getPlayerClass().getSimpleName() + ", found " + object.getClass().getSimpleName());
	}

	abstract public <T> void saveSync(T object);

	public <T> void delete(T object) {
		checkType(object);
		if (Bukkit.isPrimaryThread())
			Tasks.async(() -> deleteSync(object));
		else
			deleteSync(object);
	}

	abstract public <T> void deleteSync(T object);

	public void deleteAll() {
		if (Bukkit.isPrimaryThread())
			Tasks.async(this::deleteAllSync);
		else
			deleteAllSync();
	}

	abstract public void deleteAllSync();

	public String sanitize(String input) {
		if (Pattern.compile("[\\w\\d\\s]+").matcher(input).matches())
			return input;
		throw new InvalidInputException("Unsafe argument");
	}

}
