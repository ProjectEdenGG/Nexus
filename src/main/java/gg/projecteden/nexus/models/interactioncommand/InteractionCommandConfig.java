package gg.projecteden.nexus.models.interactioncommand;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "interaction_command", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class InteractionCommandConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private final List<InteractionCommand> interactionCommands = new ArrayList<>();
	private final transient Map<Location, InteractionCommand> locationMap = new HashMap<>();

	@PostLoad
	void postLoad() {
		for (InteractionCommand command : interactionCommands)
			locationMap.put(command.getLocation(), command);
	}

	public InteractionCommand get(Location location) {
		return locationMap.get(location);
	}

	public void add(InteractionCommand command) {
		interactionCommands.add(command);
		locationMap.put(command.getLocation(), command);
	}

	public boolean delete(Location location) {
		InteractionCommand interactionCommand = get(location);
		if (interactionCommand == null)
			return false;

		interactionCommands.remove(interactionCommand);
		locationMap.remove(interactionCommand.getLocation());
		return true;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InteractionCommand {
		@NonNull
		private Location location;
		private final Map<Integer, String> commands = new ConcurrentHashMap<>();

		public String getTrimmedCommand(String command) {
			if (isOp(command) || isConsole(command))
				return StringUtils.right(command, command.length() - 2);
			else if (isNormal(command))
				return StringUtils.right(command, command.length() - 1);
			else
				return command;
		}

		public boolean isOp(String command) {
			return command.startsWith("/^");
		}

		public boolean isConsole(String command) {
			return command.startsWith("/#");
		}

		public boolean isNormal(String command) {
			return !isOp(command) && !isConsole(command) && command.startsWith("/");
		}

		public void run(PlayerInteractEvent event) {
			int wait = 0;
			for (String original : Utils.sortByKey(commands).values()) {
				Tasks.wait(wait += 3, () -> {
					if (!event.getPlayer().isOnline())
						return;

					String command = parse(event, original);
					if (isOp(command))
						PlayerUtils.runCommandAsOp(event.getPlayer(), getTrimmedCommand(command));
					else if (isConsole(command))
						PlayerUtils.runCommandAsConsole(getTrimmedCommand(command));
					else if (isNormal(command))
						PlayerUtils.runCommand(event.getPlayer(), getTrimmedCommand(command));
					else
						Nerd.of(event.getPlayer()).sendMessage(command);
				});
			}
		}

		private String parse(PlayerInteractEvent event, String command) {
			command = command.replaceAll("\\[player]", event.getPlayer().getName());
			return command;
		}
	}
}
