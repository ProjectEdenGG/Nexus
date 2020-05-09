package me.pugabyte.bncore.models.scoreboard;

import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScoreboardService extends MongoService {
	private final static Map<UUID, ScoreboardUser> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	public List<ScoreboardUser> getActive() {
		return cache.values().stream().filter(ScoreboardUser::isActive).collect(Collectors.toList());
	}

	@Override
	public ScoreboardUser get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			ScoreboardUser user = database.createQuery(ScoreboardUser.class).field(_id).equal(uuid).first();
			if (user == null)
				user = new ScoreboardUser(uuid);
			return user;
		});

		return cache.get(uuid);
	}

	public void delete(ScoreboardUser user) {
		if (user.getScoreboard() != null)
			user.getScoreboard().delete();
		super.delete(user);
	}

}
