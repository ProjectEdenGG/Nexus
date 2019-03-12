package me.pugabyte.bncore.features.durabilitywarning;

import me.pugabyte.bncore.BNCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.features.durabilitywarning.DurabilityWarning.disabledPlayers;

/**
 * @author shannon
 */
public class DurabilityWarningCommand implements CommandExecutor {
	final static String PREFIX = BNCore.getPrefix("DurabilityWarning");

	public DurabilityWarningCommand() {
		BNCore.registerCommand("durabilitywarning", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if (!player.hasPermission("durabilitywarning.use")) {
			player.sendMessage(ChatColor.RED + "Permission denied");
			return false;
		} else {
			if (!disabledPlayers.contains(player)) {
				disabledPlayers.add(player);
				player.sendMessage(PREFIX + "Disabled");
			} else {
				disabledPlayers.remove(player);
				player.sendMessage(PREFIX + "Enabled");
			}
		}

		return true;
	}

}
