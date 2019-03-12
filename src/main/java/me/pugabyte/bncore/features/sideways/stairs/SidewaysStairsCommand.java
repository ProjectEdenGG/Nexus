package me.pugabyte.bncore.features.sideways.stairs;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.sideways.stairs.models.SidewaysStairsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.features.sideways.stairs.SidewaysStairs.playerData;

/**
 * @author shannon
 */
public class SidewaysStairsCommand implements CommandExecutor {
	final static String PREFIX = BNCore.getPrefix("SidewaysStairs");

	SidewaysStairsCommand() {
		BNCore.registerCommand("sidewaysstairs", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
		if (!(sender instanceof Player)) return false;

		Player player = (Player) sender;

		if (!playerData.containsKey(player)) {
			playerData.put(player, new SidewaysStairsPlayer(player));
		}
		SidewaysStairsPlayer swsPlayer = playerData.get(player);

		if (args.length > 0) {
			switch (args[0]) {

				case "enable":
					swsPlayer.setEnabled(true);
					player.sendMessage(PREFIX + "Enabled");
					break;

				case "disable":
					swsPlayer.setEnabled(false);
					player.sendMessage(PREFIX + "Disabled");
					break;

				case "toggle":
					if (!swsPlayer.isEnabled()) {
						swsPlayer.setEnabled(true);
						player.sendMessage(PREFIX + "Enabled");
					} else {
						swsPlayer.setEnabled(false);
						player.sendMessage(PREFIX + "Disabled");
					}
					break;

				case "setangle":
				case "set":
					if (args.length >= 2) {
						if (swsPlayer.trySetAngle(args[1])) {
							swsPlayer.setAction("set_angle");
							swsPlayer.setEnabled(true);
							player.sendMessage(PREFIX + "Angle successfully set to " + args[1]);
						} else {
							player.sendMessage(PREFIX + "Invalid angle (Must be a number between 0-7)");
						}
					}
					break;

				case "rotate":
					swsPlayer.setEnabled(true);
					player.sendMessage(PREFIX + "Angle changed to " + swsPlayer.rotate());
					break;

				case "copy":
					swsPlayer.setAction("copy");
					player.sendMessage(PREFIX + "Right click a stair block to copy its angle.");
					break;

				case "upsidedown":
					boolean enable;
					if ((args.length > 1) && (args[1].equals("false") || args[1].equals("true")))
						enable = Boolean.parseBoolean(args[1]);
					else
						enable = swsPlayer.getAction().equals("disable_upsidedown_placement");

					if (enable) {
						if (swsPlayer.getAction().equals("disable_upsidedown_placement")) {
							swsPlayer.setAction("");
							swsPlayer.setEnabled(false);
						}

						player.sendMessage(PREFIX + "Upsidedown stair placement vertical.");
					} else {
						if (!swsPlayer.getAction().equals("disable_upsidedown_placement")) {
							swsPlayer.setAction("disable_upsidedown_placement");
							swsPlayer.setEnabled(true);
						}

						player.sendMessage(PREFIX + "Upsidedown stair placement disabled.");
					}
					break;

				default:
					player.sendMessage(PREFIX + "Unknown command.");
					break;
			}
		} else {
			player.sendMessage(PREFIX + "Commands:");
			player.sendMessage(ChatColor.RED + "/sws toggle" + ChatColor.GRAY + " - Turn SWS on or off");
			player.sendMessage(ChatColor.RED + "/sws set <number>" + ChatColor.GRAY + " - Set the angle of the stairs to be placed (0-7)");
			player.sendMessage(ChatColor.RED + "/sws rotate" + ChatColor.GRAY + " - Rotate the stair angle");
			player.sendMessage(ChatColor.RED + "/sws copy" + ChatColor.GRAY + " - Copy the angle of an existing stair");
			player.sendMessage(ChatColor.RED + "/sws upsidedown [true|false]" + ChatColor.GRAY + " - Toggle upsidedown stairs");
		}

		return true;
	}

}
