package me.pugabyte.nexus.framework.persistence;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.annotations.Entity;
import dev.morphia.mapping.MapperOptions;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ChatColorConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ColorConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemMetaConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.QuestConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

public class MongoDBPersistence {
	protected static final Morphia morphia = new Morphia();
	private static Map<MongoDBDatabase, Datastore> databases = new HashMap<>();

	static {
		Nexus.getInstance().addConfigDefault("databases.mongodb.host", "localhost");
		Nexus.getInstance().addConfigDefault("databases.mongodb.port", 27017);
		Nexus.getInstance().addConfigDefault("databases.mongodb.username", "root");
		Nexus.getInstance().addConfigDefault("databases.mongodb.password", "password");
		Nexus.getInstance().addConfigDefault("databases.mongodb.prefix", "");
	}

	@SneakyThrows
	private static void openConnection(MongoDBDatabase dbType) {
		DatabaseConfig config = new DatabaseConfig("mongodb");

		// Paper compat
		morphia.getMapper().setOptions(MapperOptions.builder().classLoader(Nexus.getInstance().getClass().getClassLoader()).build());
		new Reflections("me.pugabyte.nexus.models").getTypesAnnotatedWith(Entity.class);

		MongoCredential root = MongoCredential.createScramSha1Credential(config.getUsername(), "admin", config.getPassword().toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress(), root, MongoClientOptions.builder().build());
		Datastore datastore = morphia.createDatastore(mongoClient, config.getPrefix() + dbType.getDatabase());
		morphia.getMapper().getConverters().addConverter(new ChatColorConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new ColorConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new ItemMetaConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new ItemStackConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new LocalDateConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new LocalDateTimeConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new LocationConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new QuestConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new UUIDConverter(morphia.getMapper()));
		databases.put(dbType, datastore);
	}

	public static Datastore getConnection(MongoDBDatabase bndb) {
		try {
			if (databases.get(bndb) == null)
				openConnection(bndb);
			return databases.get(bndb);
		} catch (Exception ex) {
			Nexus.severe("Could not establish connection to the MongoDB \"" + bndb.getDatabase() + "\" database: " + ex.getMessage());
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
