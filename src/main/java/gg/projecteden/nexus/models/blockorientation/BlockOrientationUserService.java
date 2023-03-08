package gg.projecteden.nexus.models.blockorientation;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BlockOrientationUser.class)
public class BlockOrientationUserService extends MongoPlayerService<BlockOrientationUser> {
	private final static Map<UUID, BlockOrientationUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, BlockOrientationUser> getCache() {
		return cache;
	}

}
