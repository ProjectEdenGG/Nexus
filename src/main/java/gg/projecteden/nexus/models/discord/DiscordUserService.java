package gg.projecteden.nexus.models.discord;

import dev.morphia.query.Query;
import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.utils.DiscordId.Role;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DiscordUser.class)
public class DiscordUserService extends MongoPlayerService<DiscordUser> {
	private final static Map<UUID, DiscordUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, DiscordUser> getCache() {
		return cache;
	}

	public DiscordUser checkVerified(String userId) {
		DiscordUser user = getFromUserId(userId);

		if (user == null || user.getUserId() == null || user.getUuid() == null || !user.getMember().getRoles().contains(Role.VERIFIED.get(Bot.KODA.jda())))
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
