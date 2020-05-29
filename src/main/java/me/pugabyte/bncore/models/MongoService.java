package me.pugabyte.bncore.models;

import dev.morphia.Datastore;
import dev.morphia.query.UpdateException;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.persistence.MongoDBDatabase;
import me.pugabyte.bncore.framework.persistence.MongoDBPersistence;
import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.Tasks;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class MongoService {
	protected static Datastore database;
	protected static String _id = "_id";

	static {
		database = MongoDBPersistence.getConnection(MongoDBDatabase.BEARNATION);
		if (database != null)
			database.ensureIndexes();
	}

	public abstract <T> Map<UUID, T> getCache();

	public void clearCache() {
		getCache().clear();
	}

	public Class<? extends PlayerOwnedObject> getPlayerClass() {
		PlayerClass annotation = getClass().getAnnotation(PlayerClass.class);
		return annotation == null ? null : annotation.value();
	}

	public <T> T get(UUID uuid) {
		Validate.notNull(getPlayerClass(), "You must provide a player owned class or override get(UUID)");

		getCache().computeIfAbsent(uuid, $ -> {
			Object object = database.createQuery(getPlayerClass()).field(_id).equal(uuid).first();
			if (object == null)
				try {
					Constructor<? extends PlayerOwnedObject> constructor = getPlayerClass().getDeclaredConstructor(UUID.class);
					constructor.setAccessible(true);
					object = constructor.newInstance(uuid);
				} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
					BNCore.log("Service not implemented correctly");
				}
			return object;
		});

		return (T) getCache().get(uuid);
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

	public <T> List<T> getAll() {
		return (List<T>) database.createQuery(getPlayerClass()).find().toList();
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
			BNCore.log("Error updating " + object.getClass().getSimpleName() + ": " + object.toString());
			ex3.printStackTrace();
		}
	}

	public <T> void delete(T object) {
		database.delete(object);
	}
}
