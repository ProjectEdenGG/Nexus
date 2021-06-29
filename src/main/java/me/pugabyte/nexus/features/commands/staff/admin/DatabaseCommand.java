package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
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
		send(service.getAll().size());
	}

	@Async
	@Path("debug <service> <uuid>")
	<T extends PlayerOwnedObject> void debug(MongoService<T> service, UUID uuid) {
		// TODO search for non quoted commas (regex), newline
		send(service.get(uuid).toPrettyString());
	}

	@Async
	@Confirm
	@Path("delete <service> <uuid>")
	<T extends PlayerOwnedObject> void delete(MongoService<T> service, UUID uuid) {
		service.delete(service.get(uuid));
		send(PREFIX + "Deleted " + Nickname.of(uuid) + " from " + service.getClass().getSimpleName());
	}

	@Async
	@Path("save <service> <uuid>")
	<T extends PlayerOwnedObject> void save(MongoService<T> service, UUID uuid) {
		service.save(service.get(uuid));
		send(PREFIX + "Saved " + Nickname.of(uuid) + " from " + service.getClass().getSimpleName());
	}

	@Async
	@Path("queueSave <service> <uuid> <delay>")
	<T extends PlayerOwnedObject> void queueSave(MongoService<T> service, UUID uuid, int delay) {
		service.queueSave(delay, service.get(uuid));
		send(PREFIX + "Queued save " + Nickname.of(uuid) + " from " + service.getClass().getSimpleName());
	}

	@Async
	@Confirm
	@Path("deleteAll <service> ")
	<T extends PlayerOwnedObject> void deleteAll(MongoService<T> service) {
		service.deleteAll();
		send(PREFIX + "Deleted all objects from " + service.getClass().getSimpleName());
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
