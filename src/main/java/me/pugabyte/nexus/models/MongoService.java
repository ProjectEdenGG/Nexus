package me.pugabyte.nexus.models;

import org.bukkit.OfflinePlayer;

public abstract class MongoService<T extends PlayerOwnedObject> extends eden.mongodb.MongoService<T> {

	static {
		loadServices("me.pugabyte.nexus.models");
	}

	public T get(OfflinePlayer player) {
		if (player == null) return null;
		return get(player.getUniqueId());
	}

}
