package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.objective.ObjectiveUser;
import gg.projecteden.nexus.models.objective.ObjectiveUser.Objective;
import gg.projecteden.nexus.models.objective.ObjectiveUserService;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

@Aliases("objective")
@Permission(Group.ADMIN)
public class ObjectivesCommand extends CustomCommand {
	private final ObjectiveUserService service = new ObjectiveUserService();

	public ObjectivesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private static final int taskId;

	static {
		final ObjectiveUserService service = new ObjectiveUserService();
		taskId = Tasks.repeatAsync(0, 2, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				final ObjectiveUser user = service.get(player);
				user.getObjectives().forEach(objective -> objective.update(user));
			}
		});
	}

	@Override
	public void _shutdown() {
		Tasks.cancel(taskId);
		for (Player player : OnlinePlayers.getAll()) {
			final ObjectiveUser user = new ObjectiveUserService().get(player);
			user.getObjectives().forEach(Objective::unsubscribe);
		}
	}

	@Path("create <player> <id> [description...]")
	@Description("Create an objective at your current location")
	void create(ObjectiveUser player, String id, String description) {
		final Objective objective = new Objective(id, description, location());
		player.add(objective);
		service.save(player);
		send(PREFIX + "Objective created");
	}

	@Path("remove <player> <objective> [--activateNext]")
	@Description("Remove an objective")
	void remove(ObjectiveUser player, @Arg(context = 1) Objective objective, @Switch boolean activateNext) {
		player.remove(objective, activateNext);
		service.save(player);
		send(PREFIX + "Objective removed");
	}

	@Path("activate <player> <objective>")
	@Description("Activate an objective")
	void activate(ObjectiveUser player, @Arg(context = 1) Objective objective) {
		player.setActiveObjective(objective);
		service.save(player);
		send(PREFIX + "Objective activated");
	}

	@Path("setDescription <player> <objective> <description>")
	@Description("Update an objective's description")
	void setDescription(ObjectiveUser player, @Arg(context = 1) Objective objective, String description) {
		objective.setDescription(description);
		service.save(player);
		send(PREFIX + "Objective description updated");
	}

	@Path("setLocation <player> <objective>")
	@Description("Update an objective's location")
	void setLocation(ObjectiveUser player, @Arg(context = 1) Objective objective) {
		objective.setLocation(location());
		service.save(player);
		send(PREFIX + "Objective location updated");
	}

	@TabCompleterFor(Objective.class)
	List<String> tabCompleteObjective(String filter, ObjectiveUser context) {
		return context.getObjectives().stream()
			.map(Objective::getId)
			.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

	@ConverterFor(Objective.class)
	Objective convertToObjective(String value, ObjectiveUser context) {
		return context.get(value);
	}

	@Getter
	@AllArgsConstructor
	public enum CompassState {
		COMPASS_00("鄀"),
		COMPASS_01("鄁"),
		COMPASS_02("鄂"),
		COMPASS_03("鄃"),
		COMPASS_04("鄄"),
		COMPASS_05("鄅"),
		COMPASS_06("鄆"),
		COMPASS_07("鄇"),
		COMPASS_08("鄈"),
		COMPASS_09("鄉"),
		COMPASS_10("鄊"),
		COMPASS_11("鄋"),
		COMPASS_12("鄌"),
		COMPASS_13("鄍"),
		COMPASS_14("鄎"),
		COMPASS_15("鄏"),
		COMPASS_16("鄐"),
		COMPASS_17("鄑"),
		COMPASS_18("鄒"),
		COMPASS_19("鄓"),
		COMPASS_20("鄔"),
		COMPASS_21("鄕"),
		COMPASS_22("鄖"),
		COMPASS_23("鄗"),
		COMPASS_24("鄘"),
		COMPASS_25("鄙"),
		COMPASS_26("鄚"),
		COMPASS_27("鄿"),
		COMPASS_28("鄛"),
		COMPASS_29("鄝"),
		COMPASS_30("鄞"),
		COMPASS_31("鄟"),
		COMPASS_EMPTY("邿"),
		;

		private final String character;

		public static CompassState of(Player player, Location objective) {
			if (objective == null || !player.getWorld().equals(objective.getWorld()))
				return COMPASS_EMPTY;

			Vector direction = player.getEyeLocation().toVector().subtract(objective.clone().add(0.5, 0.5, 0.5).toVector()).normalize();
			final float heading = LocationUtils.toDegree(Math.atan2(direction.getX(), direction.getZ()));

			float yaw = Location.normalizeYaw(player.getLocation().getYaw());

			int index = (int) ((yaw + heading) / (360d / 32));

			if (index < 0)
				index += 32;
			else if (index > 32)
				index -= 32;

			return CompassState.values()[31 - index];
		}

	}

}
