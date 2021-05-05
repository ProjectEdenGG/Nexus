package me.pugabyte.nexus.models.discord;

import dev.morphia.query.Query;
import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(DiscordUser.class)
public class DiscordUserService extends MongoService<DiscordUser> {
	private final static Map<UUID, DiscordUser> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, DiscordUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public DiscordUser checkVerified(String userId) {
		DiscordUser user = getFromUserId(userId);

		if (user == null || user.getUserId() == null || user.getUuid() == null || !user.getMember().getRoles().contains(Role.VERIFIED.get()))
			throw new InvalidInputException("You must link your Discord and Minecraft accounts before using this command");

		return user;
	}

	public DiscordUser getFromUserId(String userId) {
		Query<DiscordUser> query = database.createQuery(DiscordUser.class);
		query.and(query.criteria("userId").equalIgnoreCase(userId));
		DiscordUser user = query.find().tryNext();
		cache(user);
		return user;
	}

	public DiscordUser getFromRoleId(String roleId) {
		Query<DiscordUser> query = database.createQuery(DiscordUser.class);
		query.and(query.criteria("roleId").equalIgnoreCase(roleId));
		DiscordUser user = query.find().tryNext();
		cache(user);
		return user;
	}

}
