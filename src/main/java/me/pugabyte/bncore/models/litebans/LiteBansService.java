package me.pugabyte.bncore.models.litebans;

import com.dieselpoint.norm.Database;
import me.pugabyte.bncore.framework.persistence.MySQLDatabase;
import me.pugabyte.bncore.framework.persistence.MySQLPersistence;
import me.pugabyte.bncore.models.MySQLService;

import java.util.HashMap;
import java.util.List;

public class LiteBansService extends MySQLService {
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

	public List<String> getAlts(String uuid) {
		return database
				.select("DISTINCT alts.name")
				.table("history")
				.innerJoin("history AS alts")
				.on("alts.ip = history.ip")
				.where("history.uuid = ?")
				.orderBy("alts.name")
				.args(uuid)
				.results(String.class);
	}

	public boolean isBanned(String uuid) {
		List<HashMap> bans = database.table("bans").where("uuid = ?").and("active = 1").args(uuid).results(HashMap.class);
		return bans.size() > 0;
	}
}
