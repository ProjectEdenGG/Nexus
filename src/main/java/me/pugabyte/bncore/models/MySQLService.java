package me.pugabyte.bncore.models;

import com.dieselpoint.norm.Database;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.persistence.MySQLDatabase;
import me.pugabyte.bncore.framework.persistence.MySQLPersistence;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class MySQLService {
	protected static Database database;

	static {
		database = MySQLPersistence.getConnection(MySQLDatabase.BEARNATION);
	}

	public <T> T get(String uuid) {
		throw new UnsupportedOperationException();
	}

	public <T> T get(UUID uuid) {
		return (T) get(uuid.toString());
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

	public <T> void save(T object) {
		Tasks.async(() -> saveSync(object));
	}

	public <T> void saveSync(T object) {
		long startTime = System.currentTimeMillis();
		database.upsert(object);
		long time = System.currentTimeMillis() - startTime;
		if (time > 500)
			BNCore.warn(object.getClass().getSimpleName() + " save time took " + time + "ms");
	}

	protected String asList(List<String> list) {
		return "'" + String.join("','", list) + "'";
	}

	public String safe(String input) {
		if (Pattern.compile("[\\w\\d\\s]+").matcher(input).matches())
			return input;
		throw new InvalidInputException("Unsafe argument");
	}
}
