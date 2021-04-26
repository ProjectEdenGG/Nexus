package me.pugabyte.nexus.models.mutemenu;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(MuteMenuUser.class)
public class MuteMenuService extends MongoService<MuteMenuUser> {
	private final static Map<UUID, MuteMenuUser> cache = new HashMap<>();

	@Override
	public Map<UUID, MuteMenuUser> getCache() {
		return cache;
	}
}
