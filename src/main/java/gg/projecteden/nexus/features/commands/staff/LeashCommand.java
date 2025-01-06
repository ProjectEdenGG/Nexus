package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Permission(Group.STAFF)
@Description("Automatically follow a player")
public class LeashCommand extends CustomCommand {
	private static HashMap<UUID, Integer> leashes = new HashMap<>();
	private static double velocity = .8;

	public LeashCommand(CommandEvent event) {
		super(event);
	}

	@Path("<target>")
	@Description("Leash yourself to a player")
	void leash(Player target) {
		if (leashes.containsKey(uuid()))
			error("You are already leashed to another player");

		send(PREFIX + "&3You are now leashed to &e" + target.getPlayer().getDisplayName());
		startLeash(player(), target);
	}

	@Path("(stop|cancel)")
	@Description("Unleash yourself from a player")
	void stop() {
		if (!leashes.containsKey(uuid()))
			error("You are not currently leashed to a player. Use &c/leash <player>");

		stopLeash(player(), "&3You are no longer leashed to the player.");
	}

	@Path("(stopAll|cancelAll)")
	@Description("Cancel all server leashes")
	void stopAll() {
		for (Map.Entry<UUID, Integer> leash : leashes.entrySet())
			stopLeash(PlayerUtils.getPlayer(leash.getKey()).getPlayer(), "Leash cancelled by &e" + sender().getName());
		send(PREFIX + "All leashed cancelled");
	}

	@Path("setVelocity <velocity>")
	@Description("Set the server's leash pull velocity")
	void setVelocity(double velocity) {
		LeashCommand.velocity = velocity;
		send(PREFIX + "&3Velocity multiplier set to &e" + velocity);
	}

	private void stopLeash(Player staff, String message) {
		Tasks.cancel(leashes.get(staff.getUniqueId()));
		leashes.remove(staff.getUniqueId());
		send(staff, PREFIX + message);
	}

	private void startLeash(Player staff, Player target) {
		int taskId = Tasks.repeat(10, 30, () -> {
			if (!staff.isOnline() || !target.isOnline()) {
				stopLeash(staff, "&3Leash cancelled. &e" + target.getDisplayName() + " &3is no longer online.");
				return;
			}

			if (staff.getWorld() != target.getWorld()) {
				stopLeash(staff, "&3Leash cancelled. &e" + target.getDisplayName() + " &3is no longer in the same world.");
				return;
			}

			final Location staffLocation = staff.getLocation();
			final Location targetLocation = target.getLocation();

			final Distance distance = Distance.distance(staffLocation, targetLocation);
			if (distance.lte(7))
				return;

			if (distance.gte(100)) {
				staff.teleportAsync(targetLocation);
				return;
			}

			Vector vector = targetLocation.toVector().subtract(staffLocation.toVector()).normalize();
			double multiplier = distance.getRealDistance() / 100 + velocity;
			staff.setVelocity(vector.multiply(multiplier));
		});

		leashes.put(staff.getUniqueId(), taskId);
	}

}
