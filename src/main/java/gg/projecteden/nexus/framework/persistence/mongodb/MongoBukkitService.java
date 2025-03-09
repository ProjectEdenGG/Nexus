package gg.projecteden.nexus.framework.persistence.mongodb;

import dev.morphia.mapping.MappingException;
import dev.morphia.query.Sort;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.QueuedTask;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Debug.DebugType.DATABASE;

public abstract class MongoBukkitService<T extends DatabaseObject> extends gg.projecteden.api.mongodb.MongoService<T> {

	protected String pretty(T object) {
		return object.getUniqueId().toString();
	}

	@Override
	public void save(T object) {
		if (Bukkit.isPrimaryThread())
			Tasks.async(() -> super.save(object));
		else
			super.save(object);
	}

	@Override
	@SneakyThrows
	protected void handleSaveException(T object, Exception ex, String type) {
		if (isCME(ex))
			throw ex;

		super.handleSaveException(object, ex, type);
	}

	private boolean isCME(Exception ex) {
		return ex instanceof ConcurrentModificationException ||
				(ex instanceof MappingException && ex.getCause() instanceof ConcurrentModificationException);
	}

	@Override
	public void saveSync(T object) {
		Debug.log(DATABASE, "[" + getClassName() + "] saveSync " + object.getUuid());
		try {
			super.saveSync(object);
		} catch (Exception ex) {
			if (!isCME(ex))
				throw ex;

			final String CME = "[Mongo] Caught CME saving " + pretty(object) + "'s " + object.getClass().getSimpleName() + ", retrying";
			if (object instanceof Mailer) {
				Nexus.log(CME);
				Debug.log(DATABASE, ex);
			} else
				Debug.log(DATABASE, CME);
			queueSaveSync(TickTime.SECOND.x(3), object);
		}
	}

	public void queueSave(long delayTicks, T object) {
		Tasks.async(() -> queueSaveSync(delayTicks, object));
	}

	public void queueSaveSync(long delayTicks, T object) {
		QueuedTask.builder()
			.uuid(object.getUuid())
			.type("mongo save " + object.getClass().getSimpleName())
			.task(() -> saveSync(object))
			.completeBeforeShutdown(true)
			.queue(delayTicks);
	}

	public void delete(T object) {
		if (Bukkit.isPrimaryThread())
			Tasks.async(() -> super.delete(object));
		else
			super.delete(object);
	}

	public void deleteAll() {
		if (Bukkit.isPrimaryThread())
			Tasks.async(super::deleteAll);
		else
			super.deleteAll();
	}



	@Override
	protected T getNoCache(UUID uuid) {
		Debug.log(DATABASE, "[" + getClassName() + "] getNoCache " + uuid);
		return super.getNoCache(uuid);
	}

	@Override
	public List<T> getPage(int page, int amount) {
		Debug.log(DATABASE, "[" + getClassName() + "] getPage");
		return super.getPage(page, amount);
	}

	@Override
	public List<T> getAll() {
		Debug.log(DATABASE, "[" + getClassName() + "] getAll");
		return super.getAll();
	}

	@Override
	public List<T> getAllSortedBy(Sort... sorts) {
		Debug.log(DATABASE, "[" + getClassName() + "] getAllSortedBy");
		return super.getAllSortedBy(sorts);
	}

	@Override
	public List<T> getAllSortedByLimit(int limit, Sort... sorts) {
		Debug.log(DATABASE, "[" + getClassName() + "] getAllSortedByLimit");
		return super.getAllSortedByLimit(limit, sorts);
	}

	@Override
	public void deleteSync(T object) {
		Debug.log(DATABASE, "[" + getClassName() + "] deleteSync " + object.getUuid());
		super.deleteSync(object);
	}

	private String getClassName() {
		return getClass().getSimpleName();
	}

}
