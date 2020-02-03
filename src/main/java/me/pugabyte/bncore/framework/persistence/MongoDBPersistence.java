package me.pugabyte.bncore.framework.persistence;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;

import java.util.HashMap;
import java.util.Map;

public class MongoDBPersistence {
	protected static final Morphia morphia = new Morphia();
	private static Map<MongoDBDatabase, Datastore> databases = new HashMap<>();

	static {
		BNCore.getInstance().addConfigDefault("databases.mongodb.host", "localhost");
		BNCore.getInstance().addConfigDefault("databases.mongodb.port", 27017);
		BNCore.getInstance().addConfigDefault("databases.mongodb.username", "root");
		BNCore.getInstance().addConfigDefault("databases.mongodb.password", "password");
		BNCore.getInstance().addConfigDefault("databases.mongodb.prefix", "");
	}

	@SneakyThrows
	private static void openConnection(MongoDBDatabase dbType) {
		DatabaseConfig config = new DatabaseConfig("mongodb");

		MongoCredential root = MongoCredential.createScramSha1Credential(config.getUsername(), "admin", config.getPassword().toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress(), root, MongoClientOptions.builder().build());
		Datastore datastore = morphia.createDatastore(mongoClient, dbType.getDatabase());
		databases.put(dbType, datastore);
	}

	public static Datastore getConnection(MongoDBDatabase bndb) {
		try {
			if (databases.get(bndb) == null)
				openConnection(bndb);
			return databases.get(bndb);
		} catch (Exception ex) {
			BNCore.severe("Could not establish connection to the MongoDB \"" + bndb.getDatabase() + "\" database: " + ex.getMessage());
			return null;
		}
	}

	public static void shutdown() {
		databases.values().forEach(datastore -> {
			try {
				datastore.getMongo().close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}


}
