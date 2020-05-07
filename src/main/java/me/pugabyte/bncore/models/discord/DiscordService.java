package me.pugabyte.bncore.models.discord;

import me.pugabyte.bncore.models.MySQLService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordService extends MySQLService {
	private final static Map<String, DiscordUser> cache = new HashMap<>();

	public static void clearCache() {
		cache.clear();
	}

	@Override
	public DiscordUser get(String uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			DiscordUser user = database.where("uuid = ?", uuid).first(DiscordUser.class);
			if (user.getUuid() == null)
				return new DiscordUser(uuid);
			return user;
		});

		return cache.get(uuid);
	}

	public DiscordUser getFromUserId(String userId) {
		DiscordUser user = database.where("userId = ?", userId).first(DiscordUser.class);
		if (user.getUuid() == null)
			return null;
		return user;
	}

	public List<DiscordUser> getAll() {
		return database.results(DiscordUser.class);
	}

}
