package gg.projecteden.nexus.models.mutemenu;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MuteMenuUser.class)
public class MuteMenuService extends MongoPlayerService<MuteMenuUser> {
	private final static Map<UUID, MuteMenuUser> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, MuteMenuUser> getCache() {
		return cache;
	}
}
