package gg.projecteden.nexus.features.commands.staff.admin;

import com.mongodb.MongoNamespace;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.Utils;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static gg.projecteden.utils.StringUtils.isUuid;

@Aliases("db")
@Permission("group.admin")
public class DatabaseCommand extends CustomCommand {

	public DatabaseCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("count <service>")
	<T extends PlayerOwnedObject> void count(MongoService<T> service) {
		send(PREFIX + "Objects stored in " + name(service) + ": &e" + service.getAll().size());
	}

	@Async
	@Path("countCache <service>")
	<T extends PlayerOwnedObject> void countCache(MongoService<T> service) {
		send(PREFIX + "Objects cached in " + name(service) + ": &e" + service.getCache().size());
	}

	@Async
	@Path("countAll [page]")
	void countAll(@Arg("1") int page) {
		Map<MongoService<? extends PlayerOwnedObject>, Integer> counts = new HashMap<>() {{
			for (MongoService<? extends PlayerOwnedObject> service : services.values()) {
				int count = service.getAll().size();
				if (count > 0)
					put(service, count);
			}
		}};

		if (counts.isEmpty())
			error("No objects cached");

		send(PREFIX + "Stored objects");

		final BiFunction<MongoService<? extends PlayerOwnedObject>, String, JsonBuilder> formatter = (service, index) ->
			json(index + " &e" + name(service) + " &7- " + counts.get(service));

		paginate(Utils.sortByValueReverse(counts).keySet(), formatter, "/database count all", page);
	}

	@Async
	@Path("countAllCaches [page]")
	void countAllCaches(@Arg("1") int page) {
		Map<MongoService<? extends PlayerOwnedObject>, Integer> counts = new HashMap<>() {{
			for (MongoService<? extends PlayerOwnedObject> service : services.values()) {
				int count = service.getCache().size();
				if (count > 0)
					put(service, count);
			}
		}};

		if (counts.isEmpty())
			error("No objects stored");

		send(PREFIX + "Cached objects");

		final BiFunction<MongoService<? extends PlayerOwnedObject>, String, JsonBuilder> formatter = (service, index) ->
			json("&e" + name(service) + " &7- " + service.getCache().size());

		paginate(Utils.sortByValueReverse(counts).keySet(), formatter, "/database count all cache", page);
	}

	@Async
	@Path("debug <service> <uuid>")
	<T extends PlayerOwnedObject> void debug(MongoService<T> service, UUID uuid) {
		send(service.asPrettyJson(uuid));
	}

	@Async
	@Path("debugCache <service> <uuid>")
	<T extends PlayerOwnedObject> void debugCache(MongoService<T> service, UUID uuid) {
		send(service.get(uuid).toPrettyString());
	}

	@Async
	@Path("createQuery <service> <uuid>")
	<T extends PlayerOwnedObject> void createQuery(MongoService<T> service, UUID uuid) {
		final MongoNamespace namespace = service.getCollection().getNamespace();
		String queryString = "db.getSiblingDB(\"%s\").%s.find({\"_id\":\"%s\"}).pretty();";
		String query = String.format(queryString, namespace.getDatabaseName(), namespace.getCollectionName(), uuid.toString());
		send(json(query).copy(query).hover("&fClick to copy"));
	}

	@Async
	@Path("cacheAll <service>")
	<T extends PlayerOwnedObject> void cacheAll(MongoService<T> service) {
		int count = 0;
		for (T object : service.getAll())
			if (!service.isCached(object)) {
				++count;
				service.cache(object);
			}

		send(PREFIX + "Cached &e" + count + " &3objects to &e" + name(service));
	}

	@Async
	@Path("clearCache <service>")
	<T extends PlayerOwnedObject> void clearCache(MongoService<T> service) {
		service.clearCache();
		send(PREFIX + "Cache of &e" + name(service) + " &3cleared");
	}

	@Async
	@Path("save <service> <uuid>")
	<T extends PlayerOwnedObject> void save(MongoService<T> service, UUID uuid) {
		service.save(service.get(uuid));
		send(PREFIX + "Saved &e" + Nickname.of(uuid) + " &3to &e" + name(service));
	}

	@Async
	@Path("queueSave <service> <uuid> <delay>")
	<T extends PlayerOwnedObject> void queueSave(MongoService<T> service, UUID uuid, int delay) {
		service.queueSave(delay, service.get(uuid));
		send(PREFIX + "Queued save of &e" + Nickname.of(uuid) + " to &3" + name(service));
	}

	@Async
	@Path("saveCache <service> <threads>")
	<T extends PlayerOwnedObject> void saveCache(MongoService<T> service, @Arg("100") int threads) {
		service.saveCache(threads);
		send(PREFIX + "Saved &e" + service.getCache().size() + " &3cached objects to &e" + name(service));
	}

	@Async
	@Confirm
	@Path("delete <service> <uuid>")
	<T extends PlayerOwnedObject> void delete(MongoService<T> service, UUID uuid) {
		service.delete(service.get(uuid));
		send(PREFIX + "Deleted &e" + Nickname.of(uuid) + " &3from &e" + name(service));
	}

	@Async
	@Confirm
	@Path("deleteAll <service> ")
	<T extends PlayerOwnedObject> void deleteAll(MongoService<T> service) {
		service.deleteAll();
		send(PREFIX + "Deleted all objects from &e" + name(service));
	}

	@NotNull
	private <T extends PlayerOwnedObject> String name(MongoService<T> service) {
		return service.getClass().getSimpleName();
	}

	public static final Map<String, String> caseMap = new HashMap<>();
	public static final Map<String, MongoService<? extends PlayerOwnedObject>> services = new HashMap<>();

	static {
		final String packageName = Nexus.class.getPackage().getName() + ".models";
		Reflections reflections = new Reflections(packageName);
		for (Class<? extends MongoService> service : reflections.getSubTypesOf(MongoService.class)) {
			try {
				final String className = service.getSimpleName();
				services.put(className, service.newInstance());
				caseMap.put(className.toLowerCase(), className);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@ConverterFor(MongoService.class)
	MongoService<? extends PlayerOwnedObject> convertToMongoService(String value) {
		if (!caseMap.containsKey(value.toLowerCase()))
			error("Service &e" + value + " &cnot found");
		return services.get(caseMap.get(value.toLowerCase()));
	}

	@TabCompleterFor(MongoService.class)
	List<String> tabCompleteMongoService(String value) {
		return services.keySet().stream()
			.filter(serviceName -> serviceName.toLowerCase().startsWith(value.toLowerCase()))
			.collect(Collectors.toList());
	}

	@ConverterFor(UUID.class)
	UUID convertToUUID(String value) {
		if (isNullOrEmpty(value))
			return null;

		if ("0".equals(value))
			return StringUtils.getUUID0();

		if (isUuid(value))
			return UUID.fromString(value);

		return PlayerUtils.getPlayer(value).getUniqueId();
	}

	@TabCompleterFor(UUID.class)
	List<String> tabCompleteUUID(String filter) {
		return tabCompleteOfflinePlayer(filter);
	}

}
