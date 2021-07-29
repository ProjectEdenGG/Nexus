package gg.projecteden.nexus;

import com.google.gson.GsonBuilder;
import dev.morphia.converters.TypeConverter;
import gg.projecteden.EdenAPI;
import gg.projecteden.mongodb.DatabaseConfig;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ChannelConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ChatColorConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ColorConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.CostumeConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.JobConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.MobHeadConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.PrivateChannelConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.PublicChannelConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.QuestConverter;
import gg.projecteden.nexus.utils.SerializationUtils.JSON.LocalDateTimeGsonSerializer;
import gg.projecteden.nexus.utils.SerializationUtils.JSON.LocationGsonSerializer;
import gg.projecteden.utils.Env;
import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
				.modelPath("gg.projecteden.nexus.models")
				.env(getEnv())
				.build();
	}

	@Override
	public ClassLoader getClassLoader() {
		return Nexus.getInstance().getClass().getClassLoader();
	}

	@Override
	public Collection<? extends Class<? extends TypeConverter>> getMongoConverters() {
		return List.of(
				ChannelConverter.class,
				ChatColorConverter.class,
				ColorConverter.class,
				CostumeConverter.class,
				ItemStackConverter.class,
				JobConverter.class,
				LocationConverter.class,
				MobHeadConverter.class,
				QuestConverter.class,
				PrivateChannelConverter.class,
				PublicChannelConverter.class
		);
	}

	@Override
	public GsonBuilder getPrettyPrinter() {
		return super.getPrettyPrinter()
			.registerTypeAdapter(Location.class, new LocationGsonSerializer())
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonSerializer());
	}

}
