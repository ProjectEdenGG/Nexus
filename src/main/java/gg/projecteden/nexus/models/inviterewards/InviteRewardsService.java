package gg.projecteden.nexus.models.inviterewards;

import dev.morphia.query.Query;
import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(InviteRewards.class)
public class InviteRewardsService extends MongoService<InviteRewards> {
	private final static Map<UUID, InviteRewards> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, InviteRewards> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public boolean hasBeenInvited(UUID uuid) {
		Query<InviteRewards> query = database.createQuery(InviteRewards.class);
		query.and(query.criteria("invited").hasThisOne(uuid.toString()));
		return query.find().hasNext();
	}

}
