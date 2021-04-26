package me.pugabyte.nexus.models.litebans;

import com.dieselpoint.norm.Database;
import me.pugabyte.nexus.framework.persistence.MySQLDatabase;
import me.pugabyte.nexus.framework.persistence.MySQLPersistence;
import me.pugabyte.nexus.models.MySQLService;

import java.util.List;

public class LiteBansService extends MySQLService {
	protected static Database database = MySQLPersistence.getConnection(MySQLDatabase.LITEBANS);

	public <T extends LiteBansPunishment> List<T> getAllPunishments(Class<T> clazz) {
		return database.results(clazz);
	}

	public List<LiteBansIPHistory> getIpHistory() {
		return database.results(LiteBansIPHistory.class);
	}
}
