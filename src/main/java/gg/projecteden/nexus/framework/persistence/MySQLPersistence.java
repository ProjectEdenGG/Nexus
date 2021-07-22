package gg.projecteden.nexus.framework.persistence;

import com.dieselpoint.norm.Database;
import com.dieselpoint.norm.sqlmakers.MySqlMaker;
import gg.projecteden.mongodb.DatabaseConfig;
import gg.projecteden.nexus.Nexus;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class MySQLPersistence {
	private static Map<MySQLDatabase, Database> databases = new HashMap<>();

	@SneakyThrows
	private static void openConnection(MySQLDatabase db) {
		Class.forName("com.mysql.jdbc.Driver");

		DatabaseConfig config = DatabaseConfig.builder()
				.password(Nexus.getInstance().getConfig().getString("databases.mysql.password"))
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

	public static Database getConnection(MySQLDatabase db) {
		try {
			if (databases.get(db) == null)
				openConnection(db);
			return databases.get(db);
		} catch (Exception ex) {
			Nexus.severe("Could not establish connection to the MySQL database \"" + db.getDatabase() + "\": " + ex.getMessage());
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
