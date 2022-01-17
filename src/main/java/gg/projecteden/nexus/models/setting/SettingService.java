package gg.projecteden.nexus.models.setting;

import gg.projecteden.nexus.framework.persistence.mysql.MySQLService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingService extends MySQLService {

	public Setting get(OfflinePlayer player, String type) {
		return get(player.getUniqueId().toString(), type);
	}

	public Setting get(String id, String type) {
		Setting setting = database.table("setting").where("id = ?").and("type = ?").args(id, type).first(Setting.class);
		if (setting.getId() == null)
			setting = new Setting(id, type, null);
		return setting;
	}

	public List<Setting> getFromType(String type) {
		return database.table("setting").where("type = ?").args(type).results(Setting.class);
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
