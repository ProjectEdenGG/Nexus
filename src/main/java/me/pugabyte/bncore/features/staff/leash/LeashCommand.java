package me.pugabyte.bncore.features.staff.leash;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

@Aliases("leash")
@Permission("playerleash.followplayer")
@NoArgsConstructor
public class LeashCommand extends CustomCommand {
	private HashMap<UUID, Integer> playerRunnables = BNCore.leash.playerRunnables;

	public LeashCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void stop() {
		if (!playerRunnables.containsKey(player().getUniqueId()))
			error("You are not currently leashed to a player. Use &c/leash <player>");

		Bukkit.getScheduler().cancelTask(playerRunnables.get(player().getUniqueId()));
		playerRunnables.remove(player().getUniqueId());
		reply(PREFIX + "&3You are no longer leashed to the player.");
	}

	@Path("{player}")
	void leash(@Arg Player target) {
		reply(PREFIX + "&3You are now leashed to &e" + target.getPlayer().getDisplayName());
		runnable(player(), target);
	}

	@Path("setvelocity {double}")
	void setVelocity(@Arg double velocity) {
		BNCore.leash.setVelocity(velocity);
		reply(PREFIX + "&3Velocity multiplier set to &e" + velocity);
	}

	private void runnable(Player staff, Player target) {
		int taskID = BNCore.repeat(10, 30, () -> {
			if (!staff.isOnline() || !target.isOnline()) {
				Bukkit.getScheduler().cancelTask(playerRunnables.get(staff.getUniqueId()));
				playerRunnables.remove(staff.getUniqueId());
				send(staff, "Leash cancelled");
				return;
			}

			if(staff.getWorld() != target.getWorld()){
				Bukkit.getScheduler().cancelTask(playerRunnables.get(staff.getUniqueId()));
				playerRunnables.remove(staff.getUniqueId());
				send(staff, "&3Leash Canceled. &e" + target.getDisplayName() + " &3is no longer in the same world.");
				return;
			}

			if (staff.getLocation().distance(target.getLocation()) <= 7) {
				return;
			}

			Vector vector = target.getLocation().toVector().subtract(staff.getLocation().toVector()).normalize();
			double multiplier = staff.getLocation().distance(target.getLocation()) / 100 + BNCore.leash.getVelocity();
			staff.setVelocity(vector.multiply(multiplier));
		});

		playerRunnables.put(staff.getUniqueId(), taskID);
	}

}
