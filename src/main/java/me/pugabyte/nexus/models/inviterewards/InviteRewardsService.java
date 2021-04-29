package me.pugabyte.nexus.models.inviterewards;

import dev.morphia.query.Query;
import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(InviteRewards.class)
public class InviteRewardsService extends MongoService<InviteRewards> {
	private final static Map<UUID, InviteRewards> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

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
