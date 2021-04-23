package me.pugabyte.nexus.models;

import org.bukkit.OfflinePlayer;
import org.reflections.Reflections;

public abstract class MongoService<T extends PlayerOwnedObject> extends eden.mongodb.MongoService<T> {

	static {
		loadServices(new Reflections(MongoService.class.getPackage().getName()).getSubTypesOf(eden.mongodb.MongoService.class));
	}

	public T get(OfflinePlayer player) {
		if (player == null) return null;
		return get(player.getUniqueId());
	}

}
