package me.pugabyte.bncore.framework.persistence;

import com.dieselpoint.norm.Database;
import com.dieselpoint.norm.sqlmakers.MySqlMaker;
import me.pugabyte.bncore.BNCore;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class Persistence {
	private static final BNCore bnCore = BNCore.getInstance();
	private static Map<BearNationDatabase, Database> databases = new HashMap<>();

	private static void openConnection(BearNationDatabase bndb) throws ClassNotFoundException {
		FileConfiguration config = bnCore.getConfig();
		String host = config.getString("databases.host");
		int port = config.getInt("databases.port");
		String username = config.getString("databases.username");
		String password = config.getString("databases.password");

		Class.forName("com.mysql.jdbc.Driver");

		Database database = new Database();
		database.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + bndb.getDatabase() + "?useSSL=false&relaxAutoCommit=true");
		database.setUser(username);
		database.setPassword(password);
		database.setSqlMaker(new MySqlMaker());
		database.setMaxPoolSize(3);
		databases.put(bndb, database);
	}

	public static Database getConnection(BearNationDatabase bndb) {
		try {
			if (databases.get(bndb) == null) {
				openConnection(bndb);
			}
			return databases.get(bndb);
		} catch (Exception ex) {
			bnCore.getLogger().severe("Could not establish connection to the \"" + bndb.getDatabase() + "\" database: " + ex.getMessage());
			return null;
		}
	}

	public static void shutdown() {
		databases.values().forEach(Database::close);
	}

}
