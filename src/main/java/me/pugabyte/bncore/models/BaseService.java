package me.pugabyte.bncore.models;

import com.dieselpoint.norm.Database;
import dev.morphia.Datastore;
import me.pugabyte.bncore.framework.persistence.MongoDBDatabase;
import me.pugabyte.bncore.framework.persistence.MongoDBPersistence;
import me.pugabyte.bncore.framework.persistence.MySQLDatabase;
import me.pugabyte.bncore.framework.persistence.MySQLPersistence;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public abstract class BaseService {
	protected static Database database;
	protected static Datastore datastore;

	static {
		database = MySQLPersistence.getConnection(MySQLDatabase.BEARNATION);
		datastore = MongoDBPersistence.getConnection(MongoDBDatabase.BEARNATION);
		if (datastore != null)
			datastore.ensureIndexes();
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
		database.upsert(object);
	}

	protected String asList(List<String> list) {
		return "'" + String.join("','", list) + "'";
	}

//	public String safe(String input) {
//		if (Pattern.compile("[\\w\\d\\s]+").matcher(input).matches())
//			return input;
//		throw new InvalidInputException("Unsafe argument");
//	}
}
