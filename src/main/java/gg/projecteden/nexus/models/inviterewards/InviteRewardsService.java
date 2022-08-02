package gg.projecteden.nexus.models.inviterewards;

import dev.morphia.query.Query;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(InviteRewards.class)
public class InviteRewardsService extends MongoPlayerService<InviteRewards> {
	private final static Map<UUID, InviteRewards> cache = new ConcurrentHashMap<>();

	public Map<UUID, InviteRewards> getCache() {
		return cache;
	}

	public boolean hasBeenInvited(UUID uuid) {
		Query<InviteRewards> query = database.createQuery(InviteRewards.class);
		query.and(query.criteria("invited").hasThisOne(uuid.toString()));
		try (var cursor = query.find()) {
			return cursor.hasNext();
		}
	}

}
