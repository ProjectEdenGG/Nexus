package gg.projecteden.nexus.models.mobheads;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MobHeadUser.class)
public class MobHeadUserService extends MongoPlayerService<MobHeadUser> {
	private final static Map<UUID, MobHeadUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, MobHeadUser> getCache() {
		return cache;
	}

	@Override
	protected void beforeSave(MobHeadUser user) {
		user.getData().removeIf(data -> data.getKills() == 0 && data.getHeads() == 0);
	}

}
