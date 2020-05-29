package me.pugabyte.bncore.models.chat;

import me.pugabyte.bncore.models.MongoService;
import me.pugabyte.bncore.utils.Tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatService extends MongoService {
	private final static Map<UUID, Chatter> cache = new HashMap<>();

	public Map<UUID, Chatter> getCache() {
		return cache;
	}

	@Override
	public Chatter get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			DatabaseChatter chatter = database.createQuery(DatabaseChatter.class).field(_id).equal(uuid).first();
			if (chatter == null)
				return new Chatter(uuid);
			return chatter.deserialize();
		});

		return cache.get(uuid);
	}

	public void save(Chatter chatter) {
		Tasks.async(() -> saveSync(chatter));
	}

	public void saveSync(Chatter chatter) {
		database.save(new DatabaseChatter(chatter));
	}

}
