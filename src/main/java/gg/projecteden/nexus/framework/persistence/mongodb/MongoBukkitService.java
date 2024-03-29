package gg.projecteden.nexus.framework.persistence.mongodb;

import dev.morphia.mapping.MappingException;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.QueuedTask;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.ConcurrentModificationException;

public abstract class MongoBukkitService<T extends DatabaseObject> extends gg.projecteden.api.mongodb.MongoService<T> {

	protected abstract String pretty(T object);

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
		try {
			super.saveSync(object);
		} catch (Exception ex) {
			if (!isCME(ex))
				throw ex;

			final String CME = "[Mongo] Caught CME saving " + pretty(object) + "'s " + object.getClass().getSimpleName() + ", retrying";
			if (object instanceof Mailer) {
				Nexus.log(CME);
				if (Nexus.isDebug())
					ex.printStackTrace();
			} else
				Nexus.debug(CME);
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

}
