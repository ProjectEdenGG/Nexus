package gg.projecteden.nexus;

import com.google.gson.GsonBuilder;
import dev.morphia.converters.TypeConverter;
import gg.projecteden.DatabaseConfig;
import gg.projecteden.mongodb.EdenDatabaseAPI;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.utils.SerializationUtils.Json.LocalDateGsonSerializer;
import gg.projecteden.nexus.utils.SerializationUtils.Json.LocalDateTimeGsonSerializer;
import gg.projecteden.nexus.utils.SerializationUtils.Json.LocationGsonSerializer;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Env;
import org.bukkit.Location;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import static gg.projecteden.nexus.utils.Utils.subTypesOf;

public class API extends EdenDatabaseAPI {

	public API() {
		instance = this;
	}

	@Override
	public String getAppName() {
		return Nexus.class.getSimpleName();
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
		return subTypesOf(ItemStackConverter.class.getPackageName(), TypeConverter.class);
	}

	@Override
	public GsonBuilder getPrettyPrinter() {
		return super.getPrettyPrinter()
			.registerTypeAdapter(Location.class, new LocationGsonSerializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonSerializer());
	}

	@Override
	public void sync(Runnable runnable) {
		Tasks.sync(runnable);
	}

}
