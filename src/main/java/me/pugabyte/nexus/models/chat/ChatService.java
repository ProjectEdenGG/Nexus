package me.pugabyte.nexus.models.chat;

import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.utils.Tasks;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService extends MongoService<Chatter> {
	private final static Map<UUID, Chatter> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Chatter> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	@NotNull
	public Chatter get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			DatabaseChatter chatter = database.createQuery(DatabaseChatter.class).field(_id).equal(uuid).first();
			if (chatter == null)
				return new Chatter(uuid);
			return chatter.deserialize();
		});

		return cache.get(uuid);
	}

	@Override
	public void save(Chatter chatter) {
		Tasks.async(() -> saveSync(chatter));
	}

	@Override
	public void saveSync(Chatter chatter) {
		database.save(new DatabaseChatter(chatter));
	}

}
