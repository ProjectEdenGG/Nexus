package me.pugabyte.bncore.features.staff.leash;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.bncore.models.exceptions.preconfigured.NoPermissionException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static me.pugabyte.bncore.BNCore.colorize;

public class Leash implements CommandExecutor {
	private final String PREFIX = BNCore.getPrefix("Leash");
	private HashMap<UUID, Integer> playerRunnables = new HashMap<>();
	private double velocityMultiplier = .8;

	public Leash() {
		BNCore.registerCommand("leash", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (!(sender instanceof Player))
				throw new MustBeIngameException();

			Player player = (Player) sender;
			if (!player.hasPermission("playerleash.followplayer"))
				throw new NoPermissionException();

			if (playerRunnables.containsKey(player.getUniqueId())) {
				Bukkit.getScheduler().cancelTask(playerRunnables.get(player.getUniqueId()));
				playerRunnables.remove(player.getUniqueId());
				player.sendMessage(colorize(PREFIX + "&3You are no longer leashed to the player."));
				return true;
			}

			if (args.length < 1)
				throw new InvalidInputException("&cPlease specify a player to follow.");

			OfflinePlayer target = BNCore.getPlayer(args[0]);
			if (target != null) {
				player.sendMessage(colorize(PREFIX + "&3You are now leashed to &e" + target.getPlayer().getDisplayName()));
				runnable(player, target.getPlayer());
			} else {
				try {
					velocityMultiplier = Double.parseDouble(args[0]);
					player.sendMessage(colorize(PREFIX + "&3Velocity multiplier set to &e" + velocityMultiplier));
					return true;
				} catch (NumberFormatException ex) { /* Ignore */ }

				throw new InvalidInputException("&cPlayer not found!");
			}
		} catch (MustBeIngameException | NoPermissionException | InvalidInputException ex) {
			sender.sendMessage(PREFIX + colorize(ex.getMessage()));
		}
		return true;
	}

	private void runnable(Player staff, Player target) {
		int taskID = BNCore.scheduleSyncRepeatingTask(10, 30, () -> {
			if (!staff.isOnline() || !target.isOnline()) {
				Bukkit.getScheduler().cancelTask(playerRunnables.get(staff.getUniqueId()));
				playerRunnables.remove(staff.getUniqueId());
				staff.sendMessage(colorize("Leash cancelled"));
				return;
			}

			if(staff.getWorld() != target.getWorld()){
				Bukkit.getScheduler().cancelTask(playerRunnables.get(staff.getUniqueId()));
				playerRunnables.remove(staff.getUniqueId());
				staff.sendMessage(colorize("&3Leash Canceled. &e" + target.getDisplayName() + " &3is no longer in the same world."));
				return;
			}

			if (staff.getLocation().distance(target.getLocation()) <= 7) {
				return;
			}
			Vector vector = target.getLocation().toVector().subtract(staff.getLocation().toVector()).normalize();
			double multiplier = staff.getLocation().distance(target.getLocation()) / 100 + velocityMultiplier;
			staff.setVelocity(vector.multiply(multiplier));
		});

		playerRunnables.put(staff.getUniqueId(), taskID);
	}

}
