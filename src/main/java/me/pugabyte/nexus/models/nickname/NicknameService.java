package me.pugabyte.nexus.models.nickname;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Nickname.class)
public class NicknameService extends MongoService<Nickname> {
	private final static Map<UUID, Nickname> cache = new HashMap<>();

	public Map<UUID, Nickname> getCache() {
		return cache;
	}

	public Nickname getFromNickname(String nickname) {
		Nickname data = database.createQuery(Nickname.class).filter("nickname", sanitize(nickname)).find().tryNext();
		cache(data);
		return data;
	}

	public Nickname getFromQueueId(String queueId) {
		Nickname data = database.createQuery(Nickname.class).filter("nicknameHistory.nicknameQueueId", sanitize(queueId)).find().tryNext();
		cache(data);
		return data;
	}

}
