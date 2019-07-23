package me.pugabyte.bncore.models;

import org.bukkit.entity.Player;

import java.util.List;

public class BaseService {

	public Object get(Player player) {
		return get(player.getUniqueId().toString());
	}

	public Object get(String uuid) {
		return null;
	}

	protected String asList(List<String> list) {
		return "\"" + String.join("\",\"", list) + "\"";
	}
}
