package me.pugabyte.bncore.features.oldminigames.murder;

import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MurderCommand implements CommandExecutor {
	public final static String PREFIX = BNCore.getPrefix("Murder");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				if (args[0].equalsIgnoreCase("kit")) {
					((Player) sender).getInventory().addItem(MurderUtils.getKnife());
					((Player) sender).getInventory().addItem(MurderUtils.getGun());
					((Player) sender).getInventory().addItem(MurderUtils.getScrap());
					((Player) sender).getInventory().addItem(MurderUtils.getFakeScrap());
					((Player) sender).getInventory().addItem(MurderUtils.getCompass());
					((Player) sender).getInventory().addItem(MurderUtils.getTeleporter());
					((Player) sender).getInventory().addItem(MurderUtils.getAdrenaline());
					((Player) sender).getInventory().addItem(MurderUtils.getRetriever());
					sender.sendMessage(PREFIX + "Giving murder kit");
				} else {
					Bukkit.getServer().dispatchCommand(sender, "skmurder " + String.join(" ", args));
				}
			} else {
				sender.sendMessage("You must be ingame to use this command");

			}
		} catch (Exception e) {
			sender.sendMessage(PREFIX + "Error occurred");
		}

		return true;
	}
}