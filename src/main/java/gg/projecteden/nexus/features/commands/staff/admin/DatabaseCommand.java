package gg.projecteden.nexus.features.commands.staff.admin;

import com.mongodb.MongoNamespace;
import gg.projecteden.api.common.EdenAPI;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.Confirm;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.ReflectionUtils.subTypesOf;
import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;
import static gg.projecteden.api.common.utils.UUIDUtils.isUuid;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Aliases("db")
@Permission(Group.ADMIN)
public class DatabaseCommand extends CustomCommand {

	public DatabaseCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Description("Count objects stored in a collection")
	<T extends DatabaseObject> void count(MongoService<T> service) {
		send(PREFIX + "Objects stored in " + name(service) + ": &e" + service.getAll().size());
	}

	@Async
	@Description("Count cached objects")
	<T extends DatabaseObject> void countCache(MongoService<T> service) {
		send(PREFIX + "Objects cached in " + name(service) + ": &e" + service.getCache().size());
	}

	@Async
	@Description("Count objects stored in all collections")
	void countAll(@Optional("1") int page) {
		Map<MongoService<? extends DatabaseObject>, Integer> counts = new HashMap<>() {{
			for (MongoService<? extends DatabaseObject> service : services.values()) {
				int count = service.getAll().size();
				if (count > 0)
					put(service, count);
			}
		}};

		if (counts.isEmpty())
			error("No objects cached");

		send(PREFIX + "Stored objects  |  Total: " + counts.values().stream().mapToInt(Integer::valueOf).sum());

		final BiFunction<MongoService<? extends DatabaseObject>, String, JsonBuilder> formatter = (service, index) ->
			json(index + " &e" + name(service) + " &7- " + counts.get(service));

		paginate(Utils.sortByValueReverse(counts).keySet(), formatter, "/database countAll", page);
	}

	@Async
	@Description("Count all cached objects")
	void countAllCaches(@Optional("1") int page) {
		Map<MongoService<? extends DatabaseObject>, Integer> counts = new HashMap<>() {{
			for (MongoService<? extends DatabaseObject> service : services.values()) {
				int count = service.getCache().size();
				if (count > 0)
					put(service, count);
			}
		}};

		if (counts.isEmpty())
			error("No objects stored");

		send(PREFIX + "Cached objects  |  Total: " + counts.values().stream().mapToInt(Integer::valueOf).sum());

		final BiFunction<MongoService<? extends DatabaseObject>, String, JsonBuilder> formatter = (service, index) ->
			json("&e" + name(service) + " &7- " + service.getCache().size());

		paginate(Utils.sortByValueReverse(counts).keySet(), formatter, "/database countAllCaches", page);
	}

	@Async
	@Description("Print a record")
	<T extends DatabaseObject> void debug(MongoService<T> service, UUID uuid) {
		send(service.asPrettyJson(uuid));
	}

	@Async
	@Description("Print a record from the cache (may not always pretty-print)")
	<T extends DatabaseObject> void debugCache(MongoService<T> service, UUID uuid) {
		send(StringUtils.toPrettyString(service.get(uuid)));
	}

	@Async
	@Description("Create a paste-able GET query for mongocli")
	<T extends DatabaseObject> void createQuery_get(MongoService<T> service, UUID uuid) {
		final MongoNamespace namespace = service.getCollection().getNamespace();
		String queryString = "db.getSiblingDB(\"%s\").%s.find({\"_id\":\"%s\"}).pretty();";
		String query = String.format(queryString, namespace.getDatabaseName(), namespace.getCollectionName(), uuid.toString());
		send(json(query).copy(query).hover("&fClick to copy"));
	}

	@Async
	@Description("Create a paste-able DELETE query for mongocli")
	<T extends DatabaseObject> void createQuery_delete(MongoService<T> service, UUID uuid) {
		final MongoNamespace namespace = service.getCollection().getNamespace();
		String queryString = "db.getSiblingDB(\"%s\").%s.remove({\"_id\":\"%s\"});";
		String query = String.format(queryString, namespace.getDatabaseName(), namespace.getCollectionName(), uuid.toString());
		send(json(query).copy(query).hover("&fClick to copy"));
	}

	@Async
	@SneakyThrows
	@Description("Cache all stored objects to a service")
	<T extends DatabaseObject> void cacheAll(@Optional MongoService<T> service) {
		List<MongoService<T>> services = new ArrayList<>();

		if (service != null)
			services.add(service);
		else
			for (Class<? extends MongoService> clazz : MongoService.getServices())
				if (Utils.canEnable(clazz))
					services.add(clazz.getConstructor().newInstance());

		for (MongoService<T> _service : services) {
			int count = 0;
			for (T object : _service.getAll())
				if (!_service.isCached(object)) {
					++count;
					_service.cache(object);
				}

			send(PREFIX + "Cached &e" + count + " &3objects to &e" + name(_service));
		}

		if (services.size() > 1)
			send(PREFIX + "Done");
	}

	@Async
	@SneakyThrows
	@Description("Save the cache in all services")
	<T extends DatabaseObject> void saveAllCaches() {
		List<MongoService<T>> services = new ArrayList<>() {{
			for (Class<? extends MongoService> clazz : MongoService.getServices())
				if (Utils.canEnable(clazz))
					add(clazz.getConstructor().newInstance());
		}};

		for (MongoService<T> service : services) {
			service.saveCache();
			send(PREFIX + "Saved &e" + service.getCache().size() + " &3objects to &e" + name(service));
		}

		send(PREFIX + "Done");
	}

	@Async
	@Description("Clear the cache of a service")
	<T extends DatabaseObject> void clearCache(MongoService<T> service) {
		service.clearCache();
		send(PREFIX + "Cache of &e" + name(service) + " &3cleared");
	}

	@Async
	@Description("Write an object to the database")
	<T extends DatabaseObject> void save(MongoService<T> service, UUID uuid) {
		service.save(service.get(uuid));
		send(PREFIX + "Saved &e" + Nickname.of(uuid) + " &3to &e" + name(service));
	}

	@Async
	@Description("Queue saving an object to the database")
	<T extends DatabaseObject> void queueSave(MongoService<T> service, UUID uuid, int delay) {
		if (service instanceof MongoBukkitService<T> mongoBukkitService) {
			mongoBukkitService.queueSave(delay, service.get(uuid));
			send(PREFIX + "Queued save of &e" + Nickname.of(uuid) + " to &3" + name(service));
		} else {
			error("Service must be a MongoBukkitService");
		}
	}

	@Async
	@Description("Write the cache to the database")
	<T extends DatabaseObject> void saveCache(MongoService<T> service, @Optional("100") int threads) {
		service.saveCache(threads);
		send(PREFIX + "Saved &e" + service.getCache().size() + " &3cached objects to &e" + name(service));
	}

	@Async
	@Confirm
	@Description("Delete a record")
	<T extends DatabaseObject> void delete(MongoService<T> service, UUID uuid) {
		service.delete(service.get(uuid));
		send(PREFIX + "Deleted &e" + Nickname.of(uuid) + " &3from &e" + name(service));
	}

	@Async
	@Confirm
	@Description("Delete all objects from a collection")
	<T extends DatabaseObject> void deleteAll(MongoService<T> service) {
		service.deleteAll();
		send(PREFIX + "Deleted all objects from &e" + name(service));
	}

	@Async
	@Confirm
	@SneakyThrows
	@Description("Move data from one record to another")
	<T extends DatabaseObject> void move(MongoService<T> service, UUID from, UUID to) {
		final T old = service.get(from);
		final Field field = old.getClass().getDeclaredField("uuid");
		field.setAccessible(true);
		field.set(old, to);
		service.getCache().remove(to);
		service.save(old);
		service.cache(old);
		service.getCache().remove(from);
		send(PREFIX + "Moved data from &e" + Nickname.of(from) + " &3to &e" + Nickname.of(to) + " &3in &e" + name(service));
	}

	@NotNull
	private <T extends DatabaseObject> String name(MongoService<T> service) {
		return service.getClass().getSimpleName();
	}

	public static final Map<String, String> caseMap = new HashMap<>();
	public static final Map<String, MongoService<? extends DatabaseObject>> services = new HashMap<>();

	static {
		for (var service : subTypesOf(MongoService.class, "gg.projecteden")) {
			if (Modifier.isAbstract(service.getModifiers()))
				continue;

			final String className = service.getSimpleName();
			try {
				services.put(className, service.newInstance());
				caseMap.put(className.toLowerCase(), className);
			} catch (InstantiationException | IllegalAccessException ex) {
				Nexus.warn("Error caching service " + className);
				ex.printStackTrace();
			}
		}
	}

	@ConverterFor(MongoService.class)
	MongoService<? extends DatabaseObject> convertToMongoService(String value) {
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
			return UUID0;

		if ("app".equalsIgnoreCase(value))
			return EdenAPI.get().getAppUuid();

		if (isUuid(value))
			return UUID.fromString(value);

		if (value.startsWith("hash-"))
			return UUID.nameUUIDFromBytes(value.replace("hash-", "").getBytes());

		return PlayerUtils.getPlayer(value).getUniqueId();
	}

	@TabCompleterFor(UUID.class)
	List<String> tabCompleteUUID(String filter) {
		final List<String> completions = new ArrayList<>();
		if (!filter.equalsIgnoreCase("0") && !filter.equalsIgnoreCase("app"))
			completions.addAll(tabCompleteOfflinePlayer(filter));

		for (String shorthand : List.of("0", "app"))
			if (shorthand.startsWith(filter))
				completions.add(shorthand);

		final Entity targetEntity = getTargetEntity();
		if (targetEntity != null)
			completions.add(targetEntity.getUniqueId().toString());

		return completions;
	}

}
