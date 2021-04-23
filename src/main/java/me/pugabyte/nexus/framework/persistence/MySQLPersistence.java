package me.pugabyte.nexus.framework.persistence;

import com.dieselpoint.norm.Database;
import com.dieselpoint.norm.sqlmakers.MySqlMaker;
import eden.mongodb.DatabaseConfig;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;

import java.util.HashMap;
import java.util.Map;

public class MySQLPersistence {
	private static Map<MySQLDatabase, Database> databases = new HashMap<>();

	static {
		Nexus.getInstance().addConfigDefault("databases.mysql.host", "localhost");
		Nexus.getInstance().addConfigDefault("databases.mysql.port", 3306);
		Nexus.getInstance().addConfigDefault("databases.mysql.username", "root");
		Nexus.getInstance().addConfigDefault("databases.mysql.password", "password");
		Nexus.getInstance().addConfigDefault("databases.mysql.prefix", "");
	}

	@SneakyThrows
	private static void openConnection(MySQLDatabase db) {
		Class.forName("com.mysql.jdbc.Driver");

		DatabaseConfig config = DatabaseConfig.builder()
				.password(Nexus.getInstance().getConfig().getString("database.mysql.password"))
				.port(3306)
				.env(Nexus.getEnv())
				.build();

		Database database = new Database();
		database.setJdbcUrl("jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + (config.getPrefix() == null ? "" : config.getPrefix() + "_") + db.getDatabase() + "?useSSL=false&relaxAutoCommit=true&characterEncoding=UTF-8");
		database.setUser(config.getUsername());
		database.setPassword(config.getPassword());
		database.setSqlMaker(new MySqlMaker());
		database.setMaxPoolSize(3);
		databases.put(db, database);
	}

	public static Database getConnection(MySQLDatabase bndb) {
		try {
			if (databases.get(bndb) == null)
				openConnection(bndb);
			return databases.get(bndb);
		} catch (Exception ex) {
			Nexus.severe("Could not establish connection to the MySQL database \"" + bndb.getDatabase() + "\": " + ex.getMessage());
			return null;
		}
	}

	public static void shutdown() {
		databases.values().forEach(database -> {
			try {
				database.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

}
