package me.pugabyte.bncore.models.discord;

import me.pugabyte.bncore.models.BaseService;

public class DiscordService extends BaseService {

	@Override
	public DiscordUser get(String uuid) {
		DiscordUser user = database.where("uuid = ?", uuid).first(DiscordUser.class);
		if (user.getUuid() == null)
			return null;
		return user;
	}

	public DiscordUser getFromUserId(String userId) {
		DiscordUser user = database.where("userId = ?", userId).first(DiscordUser.class);
		if (user.getUuid() == null)
			return null;
		return user;
	}

}
