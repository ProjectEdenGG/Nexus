package gg.projecteden.nexus.models.locks.blocks;

import dev.morphia.query.Query;
import gg.projecteden.mongodb.MongoService;
import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.StringUtils.getShortLocationString;

@ObjectClass(BlockLock.class)
public class BlockLockService extends MongoService<BlockLock> {
	private final static Map<UUID, BlockLock> cache = new ConcurrentHashMap<>();

	public Map<UUID, BlockLock> getCache() {
		return cache;
	}

	public BlockLock get(Location location) {
		Query<BlockLock> query = database.createQuery(BlockLock.class);
		query.and(query.criteria("location").equal(LocationConverter.encode(location.toBlockLocation())));
		List<BlockLock> matches = query.find().toList();

		if (matches.isEmpty())
			return null;

		if (matches.size() > 1) {
			Nexus.warn("Found multiple locks at " + getShortLocationString(location));
			remove(location);
			return null;
		}

		BlockLock lock = matches.get(0);

		if (lock.getMaterial() != location.getBlock().getType()) {
			remove(location);
			return null;
		}

		return lock;
	}

	public boolean remove(Location location) {
		Query<BlockLock> query = database.createQuery(BlockLock.class);
		query.and(query.criteria("location").equal(LocationConverter.encode(location.toBlockLocation())));
		return database.delete(query).getN() != 0;
	}

}
