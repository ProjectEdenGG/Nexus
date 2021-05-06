package me.pugabyte.nexus.models;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import dev.morphia.mapping.MappingException;
import eden.utils.TimeUtils.Time;
import lombok.SneakyThrows;
import me.pugabyte.nexus.utils.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MongoService<T extends PlayerOwnedObject> extends eden.mongodb.MongoService<T> {

	static {
		loadServices("me.pugabyte.nexus.models");
	}

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

			queueSaveSync(Time.SECOND.x(3), object);
		}
	}

	protected abstract Map<UUID, Integer> getSaveQueue();

	public void queueSave(int delayTicks, T object) {
		Tasks.async(() -> queueSaveSync(delayTicks, object));
	}

	public void queueSaveSync(int delayTicks, T object) {
		UUID uuid = object.getUuid();
		AtomicInteger taskId = new AtomicInteger(0);

		Runnable resave = () -> {
			synchronized (object) {
				if (getSaveQueue().containsKey(uuid))
					if (getSaveQueue().get(uuid).equals(taskId.get()))
						saveSync(object);
			}
		};

		if (Bukkit.isPrimaryThread())
			taskId.set(Tasks.wait(delayTicks, resave));
		else
			taskId.set(Tasks.waitAsync(delayTicks, resave));

		getSaveQueue().put(uuid, taskId.get());
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
		for (Player player : Bukkit.getOnlinePlayers())
			online.add(get(player));
		return online;
	}

	@NotNull
	protected <U> List<U> map(AggregateIterable<Document> documents, Class<U> clazz) {
		return new ArrayList<>() {{
			for (Document purchase : documents)
				add(database.getMapper().fromDBObject(database, clazz, new BasicDBObject(purchase), null));
		}};
	}

}
