package me.pugabyte.bncore.models;

import dev.morphia.Datastore;
import dev.morphia.query.UpdateException;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.persistence.MongoDBDatabase;
import me.pugabyte.bncore.framework.persistence.MongoDBPersistence;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MongoService {
	protected static Datastore database;
	protected static String _id = "_id";

	static {
		database = MongoDBPersistence.getConnection(MongoDBDatabase.BEARNATION);
		if (database != null)
			database.ensureIndexes();
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

	public <T> void save(T object) {
		Tasks.async(() -> saveSync(object));
	}

	public <T> void saveSync(T object) {
		try {
			database.merge(object);
		} catch (UpdateException doesntExistYet) {
			try {
				database.save(object);
			} catch (Exception ex2) {
				BNCore.log("Error saving " + object.getClass().getSimpleName() + ": " + object.toString());
				ex2.printStackTrace();
			}
		} catch (Exception ex3) {
			BNCore.log("Error saving " + object.getClass().getSimpleName() + ": " + object.toString());
			ex3.printStackTrace();
		}
	}

	public <T> void delete(T object) {
		database.delete(object);
	}
}
