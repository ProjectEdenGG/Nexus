package me.pugabyte.bncore.features.leash;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.bncore.Utils.repeat;

@Permission("leash.use")
public class LeashCommand extends CustomCommand {
	private HashMap<UUID, Integer> leashes = BNCore.leash.leashes;

	public LeashCommand(CommandEvent event) {
		super(event);
	}

	@Path("{player}")
	void leash(@Arg Player target) {
		if (leashes.containsKey(player().getUniqueId()))
			error("You are already leashed to another player");

		reply(PREFIX + "&3You are now leashed to &e" + target.getPlayer().getDisplayName());
		startLeash(player(), target);
	}

	@Path("(stop|cancel)")
	void stop() {
		if (!leashes.containsKey(player().getUniqueId()))
			error("You are not currently leashed to a player. Use &c/leash <player>");

		stopLeash(player(), "&3You are no longer leashed to the player.");
	}

	@Path("(stopall|cancelall)")
	void stopAll() {
		for (Map.Entry<UUID, Integer> leash : leashes.entrySet())
			stopLeash(Utils.getPlayer(leash.getKey()).getPlayer(), "Leash cancelled by &e" + sender().getName());
		reply(PREFIX + "All leashed cancelled");
	}

	@Path("setvelocity {double}")
	void setVelocity(@Arg double velocity) {
		BNCore.leash.setVelocity(velocity);
		reply(PREFIX + "&3Velocity multiplier set to &e" + velocity);
	}

	private void stopLeash(Player staff, String message) {
		Utils.cancelTask(leashes.get(staff.getUniqueId()));
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

			if (staff.getLocation().distance(target.getLocation()) <= 7) {
				return;
			}

			Vector vector = target.getLocation().toVector().subtract(staff.getLocation().toVector()).normalize();
			double multiplier = staff.getLocation().distance(target.getLocation()) / 100 + BNCore.leash.getVelocity();
			staff.setVelocity(vector.multiply(multiplier));
		});

		leashes.put(staff.getUniqueId(), taskId);
	}

}
