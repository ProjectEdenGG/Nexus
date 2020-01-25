package me.pugabyte.bncore.models.settings;

import me.pugabyte.bncore.models.BaseService;
import org.bukkit.entity.Player;

public class SettingService extends BaseService {

	public Setting get(Player player, String type) {
		return get(player.getUniqueId().toString(), type);
	}

	public Setting get(String id, String type) {
		return database.table("setting").where("id = ?").and("type = ?").args(id, type).first(Setting.class);
	}

}
