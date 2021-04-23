package me.pugabyte.nexus.models;

import org.bukkit.OfflinePlayer;

public abstract class MongoService<T extends PlayerOwnedObject> extends eden.mongodb.MongoService<T> {

	public T get(OfflinePlayer player) {
		return get(player.getUniqueId());
	}

}
