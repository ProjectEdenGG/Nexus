package me.pugabyte.nexus.models.mutemenu;

import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MuteMenuService extends MongoService {
	private final static Map<UUID, MuteMenuUser> cache = new HashMap<>();

	@Override
	public Map<UUID, MuteMenuUser> getCache() {
		return cache;
	}
}
