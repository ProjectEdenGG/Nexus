package me.pugabyte.bncore.models.litebans;

import com.dieselpoint.norm.Database;
import me.pugabyte.bncore.framework.persistence.MySQLDatabase;
import me.pugabyte.bncore.framework.persistence.MySQLPersistence;
import me.pugabyte.bncore.models.BaseService;

public class LiteBansService extends BaseService {
	protected static Database database = MySQLPersistence.getConnection(MySQLDatabase.LITEBANS);

	public int getHistory(String uuid) {
		return database.sql("SELECT " +
				"(SELECT COUNT(*) FROM `bans` WHERE `bans`.`uuid` = ?) + " +
				"(SELECT COUNT(*) FROM `mutes` WHERE `mutes`.`uuid` = ?) + " +
				"(SELECT COUNT(*) FROM `kicks` WHERE `kicks`.`uuid` = ?) + " +
				"(SELECT COUNT(*) FROM `warnings` WHERE `warnings`.`uuid` = ?) " +
				"AS total")
				.args(uuid, uuid, uuid, uuid)
				.first(Long.class).intValue();
	}
}
