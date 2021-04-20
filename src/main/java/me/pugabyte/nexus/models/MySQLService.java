package me.pugabyte.nexus.models;

import com.dieselpoint.norm.Database;
import com.google.common.base.Strings;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.MySQLDatabase;
import me.pugabyte.nexus.framework.persistence.MySQLPersistence;

import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

public abstract class MySQLService extends DatabaseService {
	protected static Database database;

	static {
		database = MySQLPersistence.getConnection(MySQLDatabase.BEARNATION);
	}

	public String getTable() {
		Class<? extends PlayerOwnedObject> playerClass = getPlayerClass();
		Table annotation = playerClass.getAnnotation(Table.class);
		if (annotation != null && !Strings.isNullOrEmpty(annotation.name()))
			return annotation.name();
		return playerClass.getSimpleName().toLowerCase();
	}

	public <T> T get(String uuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T get(UUID uuid) {
		return get(uuid.toString());
	}

	@Override
	public <T> List<T> getAll() {
		return (List<T>) database.results(getPlayerClass());
	}

	@Override
	public <T> void saveSync(T object) {
		long startTime = System.currentTimeMillis();
		database.upsert(object);
		long time = System.currentTimeMillis() - startTime;
		if (time > 500)
			Nexus.warn(object.getClass().getSimpleName() + " save time took " + time + "ms");
	}

	@Override
	public <T> void deleteSync(T object) {
		database.delete(object);
	}

	@Override
	public void deleteAllSync() {
		database.table(getTable()).delete();
	}

	protected String asList(List<String> list) {
		return "'" + String.join("','", list) + "'";
	}

}
