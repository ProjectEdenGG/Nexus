package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Tasks.repeat;

@Permission(Group.STAFF)
public class LeashCommand extends CustomCommand {
	private static HashMap<UUID, Integer> leashes = new HashMap<>();
	private static double velocity = .8;

	public LeashCommand(CommandEvent event) {
		super(event);
	}

	@Path("(stop|cancel)")
	void stop() {
		if (!leashes.containsKey(uuid()))
			error("You are not currently leashed to a player. Use &c/leash <player>");

		stopLeash(player(), "&3You are no longer leashed to the player.");
	}

	@Path("(stopAll|cancelAll)")
	void stopAll() {
		for (Map.Entry<UUID, Integer> leash : leashes.entrySet())
			stopLeash(PlayerUtils.getPlayer(leash.getKey()).getPlayer(), "Leash cancelled by &e" + sender().getName());
		send(PREFIX + "All leashed cancelled");
	}

	@Path("<target>")
	void leash(Player target) {
		if (leashes.containsKey(uuid()))
			error("You are already leashed to another player");

		send(PREFIX + "&3You are now leashed to &e" + target.getPlayer().getDisplayName());
		startLeash(player(), target);
	}

	@Path("setVelocity <velocity>")
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
		int taskId = repeat(10, 30, () -> {
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

			if (staffLocation.distance(targetLocation) <= 7) {
				return;
			}

			if (staffLocation.distance(targetLocation) >= 100) {
				staff.teleportAsync(targetLocation);
				return;
			}

			Vector vector = targetLocation.toVector().subtract(staffLocation.toVector()).normalize();
			double multiplier = staffLocation.distance(targetLocation) / 100 + velocity;
			staff.setVelocity(vector.multiply(multiplier));
		});

		leashes.put(staff.getUniqueId(), taskId);
	}

}
