package me.pugabyte.bncore.framework.persistence;

import me.pugabyte.bncore.BNCore;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class OldPersistence {
	private static final BNCore bnCore = BNCore.getInstance();
	private static Map<BearNationDatabase, Connection> connections = new HashMap<>();

	private static void openConnection(BearNationDatabase db) throws SQLException, ClassNotFoundException {
		if (connections.get(db) != null && !connections.get(db).isClosed()) {
			return;
		}

		FileConfiguration config = bnCore.getConfig();
		String host = config.getString("databases.host");
		int port = config.getInt("databases.port");
		String username = config.getString("databases.username");
		String password = config.getString("databases.password");

		String database = db.getDatabase();

		synchronized (bnCore) {
			Class.forName("com.mysql.jdbc.Driver");

			String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&relaxAutoCommit=true";
			try {
				Connection connection = DriverManager.getConnection(url, username, password);
				connections.put(db, connection);
			} catch (Exception ex) {
				bnCore.getLogger().severe("Could not establish connection to the \"" + database + "\" database");
			}
		}
	}

	public static Connection getConnection(BearNationDatabase db) {
		try {
			openConnection(db);
			return connections.get(db);
		} catch (Exception ex) {
			bnCore.getLogger().severe("Could not establish connection to the \"" + db.getDatabase() + "\" database");
			return null;
		}
	}

}