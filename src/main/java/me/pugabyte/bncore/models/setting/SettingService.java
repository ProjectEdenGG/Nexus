package me.pugabyte.bncore.models.setting;

import me.pugabyte.bncore.models.BaseService;
import org.bukkit.entity.Player;

public class SettingService extends BaseService {

	public Setting get(Player player, String type) {
		return get(player.getUniqueId().toString(), type);
	}

	public Setting get(String id, String type) {
		Setting setting = database.table("setting").where("id = ?").and("type = ?").args(id, type).first(Setting.class);
		if (setting.getId() == null)
			setting = new Setting(id, type, null);
		return setting;
	}

	public void delete(Player player, String type) {
		delete(player.getUniqueId().toString(), type);
	}

	public void delete(Setting setting) {
		delete(setting.getId(), setting.getType());
	}

	public void delete(String id, String type) {
		database.table("setting").where("id = ?").and("type = ?").args(id, type).delete();
	}

}
