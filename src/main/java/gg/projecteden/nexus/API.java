package gg.projecteden.nexus;

import com.google.gson.GsonBuilder;
import dev.morphia.converters.TypeConverter;
import gg.projecteden.EdenAPI;
import gg.projecteden.mongodb.DatabaseConfig;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.utils.SerializationUtils.JSON.LocalDateGsonSerializer;
import gg.projecteden.nexus.utils.SerializationUtils.JSON.LocalDateTimeGsonSerializer;
import gg.projecteden.nexus.utils.SerializationUtils.JSON.LocationGsonSerializer;
import gg.projecteden.utils.Env;
import org.bukkit.Location;
import org.reflections.Reflections;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
		return new Reflections(ItemStackConverter.class.getPackage().getName()).getSubTypesOf(TypeConverter.class);
	}

	@Override
	public GsonBuilder getPrettyPrinter() {
		return super.getPrettyPrinter()
			.registerTypeAdapter(Location.class, new LocationGsonSerializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonSerializer());
	}

}
