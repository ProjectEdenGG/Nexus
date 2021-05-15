package me.pugabyte.nexus.models.interactioncommand;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.PlayerUtils.runCommand;
import static me.pugabyte.nexus.utils.PlayerUtils.runCommandAsConsole;
import static me.pugabyte.nexus.utils.PlayerUtils.runCommandAsOp;
import static me.pugabyte.nexus.utils.StringUtils.right;

@Data
@Entity("interaction_command")
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
		private final Map<Integer, String> commands = new HashMap<>();

		public String getTrimmedCommand(String command) {
			if (isOp(command) || isConsole(command))
				return right(command, command.length() - 2);
			else if (isNormal(command))
				return right(command, command.length() - 1);
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
						runCommandAsOp(event.getPlayer(), getTrimmedCommand(command));
					else if (isConsole(command))
						runCommandAsConsole(getTrimmedCommand(command));
					else if (isNormal(command))
						runCommand(event.getPlayer(), getTrimmedCommand(command));
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
