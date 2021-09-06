package gg.projecteden.nexus.models;

import dev.morphia.mapping.MappingException;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.QueuedTask;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public abstract class MongoService<T extends PlayerOwnedObject> extends gg.projecteden.mongodb.MongoService<T> {

	static {
		loadServices("gg.projecteden.nexus.models");
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
		try {
			super.saveSync(object);
		} catch (Exception ex) {
			if (!isCME(ex))
				throw ex;

			final String CME = "[Mongo] Caught CME saving " + object.getNickname() + "'s " + object.getClass().getSimpleName() + ", retrying";
			if (object instanceof Mailer)
				Nexus.log(CME);
			else
				Nexus.debug(CME);
			queueSaveSync(TickTime.SECOND.x(3), object);
		}
	}

	public void queueSave(int delayTicks, T object) {
		Tasks.async(() -> queueSaveSync(delayTicks, object));
	}

	public void queueSaveSync(int delayTicks, T object) {
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

	public List<T> getOnline() {
		List<T> online = new ArrayList<>();
		for (Player player : PlayerUtils.getOnlinePlayers())
			online.add(get(player));
		return online;
	}

}
