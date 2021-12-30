package gg.projecteden.nexus.framework.persistence.mysql;

import com.dieselpoint.norm.Database;
import com.google.common.base.Strings;
import gg.projecteden.interfaces.HasUniqueId;
import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;

import javax.persistence.Table;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class MySQLService {
	protected static Database database;

	static {
		database = MySQLPersistence.getConnection(MySQLDatabase.BEARNATION);
	}

	public Class<? extends gg.projecteden.interfaces.DatabaseObject> getPlayerClass() {
		ObjectClass annotation = getClass().getAnnotation(ObjectClass.class);
		return annotation == null ? null : annotation.value();
	}

	public <T> T get(HasUniqueId player) {
		return get(player.getUniqueId());
	}

	public String getTable() {
		Class<? extends gg.projecteden.interfaces.DatabaseObject> objectClass = getPlayerClass();
		Table annotation = objectClass.getAnnotation(Table.class);
		if (annotation != null && !Strings.isNullOrEmpty(annotation.name()))
			return annotation.name();
		return objectClass.getSimpleName().toLowerCase();
	}

	public <T> T get(String uuid) {
		throw new UnsupportedOperationException();
	}

	public <T> T get(UUID uuid) {
		return get(uuid.toString());
	}

	public <T> List<T> getAll() {
		return (List<T>) database.results(getPlayerClass());
	}

	private <T> void checkType(T object) {
		if (getPlayerClass() == null) return;
		if (!object.getClass().isAssignableFrom(getPlayerClass()))
			throw new InvalidInputException(this.getClass().getSimpleName() + " received wrong class type, expected "
				+ getPlayerClass().getSimpleName() + ", found " + object.getClass().getSimpleName());
	}

	public <T> void save(T object) {
		checkType(object);
		if (Bukkit.isPrimaryThread())
			Tasks.async(() -> saveSync(object));
		else
			saveSync(object);
	}

	public <T> void saveSync(T object) {
		long startTime = System.currentTimeMillis();
		database.upsert(object);
		long time = System.currentTimeMillis() - startTime;
		if (time > 500)
			Nexus.warn(object.getClass().getSimpleName() + " save time took " + time + "ms");
	}

	public <T> void delete(T object) {
		checkType(object);
		if (Bukkit.isPrimaryThread())
			Tasks.async(() -> deleteSync(object));
		else
			deleteSync(object);
	}

	public <T> void deleteSync(T object) {
		database.delete(object);
	}

	public void deleteAll() {
		if (Bukkit.isPrimaryThread())
			Tasks.async(this::deleteAllSync);
		else
			deleteAllSync();
	}

	public void deleteAllSync() {
		database.table(getTable()).delete();
	}

	public String sanitize(String input) {
		if (Pattern.compile("[\\w\\s]+").matcher(input).matches())
			return input;
		throw new InvalidInputException("Unsafe argument");
	}

	protected String asList(List<String> list) {
		return "'" + String.join("','", list) + "'";
	}

}
