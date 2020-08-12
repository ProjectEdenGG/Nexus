package me.pugabyte.bncore.features.store.perks.stattrack;

/*
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.store.perks.stattrack.models.StatItem;
import me.pugabyte.bncore.features.store.perks.stattrack.models.Tool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class StatTrackCommand implements CommandExecutor {
	final static String PREFIX = BNCore.getPrefix("StatTrack");
	public static Map<String, StatItem> statItems = new HashMap<>();

	public StatTrackCommand() {
		BNCore.registerCommand("stattrack", this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (args.length != 0) {
				if (args[0].equals("start")) {
					ItemStack item = player.getInventory().getItemInMainHand();
					for (Tool tool : Tool.values()) {
						if (tool.getTools().contains(item.getType())) {
							StatItem statItem = new StatItem(item);
							statItem.parse();
							statItem.write();
							player.getInventory().setItemInMainHand(statItem.getItem());

							send(player, PREFIX + "Enabled statistic tracking on " + item.getType());
							return true;
						}
					}
					send(player, PREFIX + "Statistic tracking cannot be enabled on " + item.getType());
					return true;
				}
			}
		}
		return true;
	}
}
*/
