package me.pugabyte.nexus.models.nickname;

import dev.morphia.query.Query;
import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Nickname.class)
public class NicknameService extends MongoService<Nickname> {
	private final static Map<UUID, Nickname> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, Nickname> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public Nickname getFromNickname(String nickname) {
		Query<Nickname> query = database.createQuery(Nickname.class);
		query.and(query.criteria("nickname").equalIgnoreCase(nickname));
		Nickname data = query.find().tryNext();
		cache(data);
		return data;
	}

	public Nickname getFromQueueId(String queueId) {
		Query<Nickname> query = database.createQuery(Nickname.class);
		query.and(query.criteria("nicknameHistory.nicknameQueueId").equalIgnoreCase(queueId));
		Nickname data = query.find().tryNext();
		cache(data);
		return data;
	}

}
