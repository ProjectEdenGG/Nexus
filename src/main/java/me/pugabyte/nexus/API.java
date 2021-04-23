package me.pugabyte.nexus;

import dev.morphia.converters.TypeConverter;
import eden.EdenAPI;
import eden.mongodb.DatabaseConfig;
import eden.utils.Env;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ChatColorConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ColorConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.QuestConverter;

import java.util.Arrays;
import java.util.Collection;

public class API extends EdenAPI {

	public API() {
		instance = this;
	}

	@Override
	public Env getEnv() {
		return Nexus.getEnv();
	}

	@Override
	public DatabaseConfig getDatabaseConfig() {
		return DatabaseConfig.builder()
				.password(Nexus.getInstance().getConfig().getString("databases.mongodb.password"))
				.modelPath("me.pugabyte.nexus.models")
				.build();
	}

	@Override
	public ClassLoader getClassLoader() {
		return Nexus.getInstance().getClass().getClassLoader();
	}

	@Override
	public Collection<? extends Class<? extends TypeConverter>> getMongoConverters() {
		return Arrays.asList(
				ChatColorConverter.class,
				ColorConverter.class,
				ItemStackConverter.class,
				LocationConverter.class,
				QuestConverter.class
		);
	}

}
