package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static eden.utils.StringUtils.isUuid;

@Permission("group.admin")
public class DatabaseCommand extends CustomCommand {

	public DatabaseCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("count <service>")
	<T extends PlayerOwnedObject> void count(MongoService<T> service) {
		send(PREFIX + "Objects stored in " + name(service) + ": " + service.getAll().size());
	}

	@Async
	@Path("debug <service> <uuid>")
	<T extends PlayerOwnedObject> void debug(MongoService<T> service, UUID uuid) {
		send(service.get(uuid).toPrettyString());
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

		send(PREFIX + "Cached " + count + " objects to " + name(service));
	}

	@Async
	@Path("save <service> <uuid>")
	<T extends PlayerOwnedObject> void save(MongoService<T> service, UUID uuid) {
		service.save(service.get(uuid));
		send(PREFIX + "Saved " + Nickname.of(uuid) + " to " + name(service));
	}

	@Async
	@Path("queueSave <service> <uuid> <delay>")
	<T extends PlayerOwnedObject> void queueSave(MongoService<T> service, UUID uuid, int delay) {
		service.queueSave(delay, service.get(uuid));
		send(PREFIX + "Queued save of " + Nickname.of(uuid) + " to " + name(service));
	}

	@Async
	@Path("saveCache <service> <threads>")
	<T extends PlayerOwnedObject> void saveCache(MongoService<T> service, @Arg("100") int threads) {
		service.saveCache(threads);
		send(PREFIX + "Saved " + service.getCache().size() + " cached objects to " + name(service));
	}

	@Async
	@Confirm
	@Path("delete <service> <uuid>")
	<T extends PlayerOwnedObject> void delete(MongoService<T> service, UUID uuid) {
		service.delete(service.get(uuid));
		send(PREFIX + "Deleted " + Nickname.of(uuid) + " from " + name(service));
	}

	@Async
	@Confirm
	@Path("deleteAll <service> ")
	<T extends PlayerOwnedObject> void deleteAll(MongoService<T> service) {
		service.deleteAll();
		send(PREFIX + "Deleted all objects from " + service.getClass().getSimpleName());
	}

	@NotNull
	private <T extends PlayerOwnedObject> String name(MongoService<T> service) {
		return service.getClass().getSimpleName();
	}

	@ConverterFor(UUID.class)
	UUID convertToUUID(String value) {
		if (isNullOrEmpty(value)) return null;
		if (isUuid(value)) return UUID.fromString(value);
		return PlayerUtils.getPlayer(value).getUniqueId();
	}

	@TabCompleterFor(UUID.class)
	List<String> tabCompleteUUID(String filter) {
		return tabCompleteOfflinePlayer(filter);
	}

}
