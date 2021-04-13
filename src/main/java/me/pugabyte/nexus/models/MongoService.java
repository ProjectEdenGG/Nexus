package me.pugabyte.nexus.models;

import dev.morphia.Datastore;
import dev.morphia.query.Sort;
import dev.morphia.query.UpdateException;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.persistence.MongoDBDatabase;
import me.pugabyte.nexus.framework.persistence.MongoDBPersistence;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.isV4Uuid;

public abstract class MongoService extends DatabaseService {
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

	public <T extends PlayerOwnedObject> void cache(T object) {
		if (object != null)
			getCache().put(object.getUuid(), object);
	}

	@Override
	@NotNull
	public <T> T get(UUID uuid) {
//		if (isEnableCache())
			return (T) getCache(uuid);
//		else
//			return getNoCache(uuid);
	}

	@NotNull
	protected <T extends PlayerOwnedObject> T getCache(UUID uuid) {
		Validate.notNull(getPlayerClass(), "You must provide a player owned class or override get(UUID)");
		if (getCache().containsKey(uuid) && getCache().get(uuid) == null)
			getCache().remove(uuid);
		getCache().computeIfAbsent(uuid, $ -> getNoCache(uuid));
		return (T) getCache().get(uuid);
	}

	protected <T extends PlayerOwnedObject> T getNoCache(UUID uuid) {
		Object object = database.createQuery(getPlayerClass()).field(_id).equal(uuid).first();
		if (object == null)
			object = createPlayerObject(uuid);
		if (object == null)
			Nexus.log("New instance of " + getPlayerClass().getSimpleName() + " is null");
		return (T) object;
	}

	protected Object createPlayerObject(UUID uuid) {
		try {
			Constructor<? extends PlayerOwnedObject> constructor = getPlayerClass().getDeclaredConstructor(UUID.class);
			constructor.setAccessible(true);
			return constructor.newInstance(uuid);
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
			ex.printStackTrace();
			throw new NexusException(this.getClass().getSimpleName() + " not implemented correctly");
		}
	}

	@Override
	public <T> List<T> getAll() {
		return (List<T>) database.createQuery(getPlayerClass()).find().toList();
	}

	public <T> List<T> getAllSortedBy(Sort... sorts) {
		return (List<T>) database.createQuery(getPlayerClass())
				.order(sorts)
				.find().toList();
	}

	public <T> List<T> getAllSortedByLimit(int limit, Sort... sorts) {
		return (List<T>) database.createQuery(getPlayerClass())
				.order(sorts)
				.limit(limit)
				.find().toList();
	}

	@Override
	public <T> void saveSync(T object) {
		PlayerOwnedObject playerOwnedObject = (PlayerOwnedObject) object;
		if (!isV4Uuid(playerOwnedObject.getUuid()) && !playerOwnedObject.getUuid().equals(Nexus.getUUID0()))
			return;

		try {
			database.merge(object);
		} catch (UpdateException doesntExistYet) {
			try {
				database.save(object);
			} catch (Exception ex2) {
				String toString = object.toString();
				Nexus.log("Error saving " + object.getClass().getSimpleName() + (toString.length() >= Short.MAX_VALUE ? "" : ": " + toString));
				ex2.printStackTrace();
			}
		} catch (Exception ex3) {
			String toString = object.toString();
			Nexus.log("Error updating " + object.getClass().getSimpleName() + (toString.length() >= Short.MAX_VALUE ? "" : ": " + toString));
			ex3.printStackTrace();
		}
	}

	@Override
	public <T> void deleteSync(T object) {
		PlayerOwnedObject playerOwnedObject = (PlayerOwnedObject) object;

		if (!isV4Uuid(playerOwnedObject.getUuid()) && !playerOwnedObject.getUuid().equals(Nexus.getUUID0()))
			return;

		database.delete(object);
		getCache().remove(playerOwnedObject.getUuid());
	}

	@Override
	public void deleteAllSync() {
		database.getCollection(getPlayerClass()).drop();
		clearCache();
	}

	/*
	public void log(String name) {
		try {
			try {
				throw new BNException("Stacktrace");
			} catch (BNException ex) {
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				Nexus.fileLogSync("pugmas-db-debug", "[Primary thread: " + Bukkit.isPrimaryThread() + "] MongoDB Pugmas20 " + name + "\n" + sw.toString() + "\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	*/
}
