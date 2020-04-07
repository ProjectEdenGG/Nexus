package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Permission("permissions.manage")
public class PermHelperCommand extends CustomCommand {

	PermHelperCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		send(PREFIX + "Correct usage: /permhelper <npcs|homes|plots|vaults> <add|remove> <player> <amount>");
	}

	@Path("(npcs|homes|plots|vaults) (add|remove) <player> <amount>")
	void modify(OfflinePlayer player, int amount) {
		if (arg(2).equals("remove")) amount = -amount;
		modify(arg(1).toLowerCase(), player, amount);
	}

	private void modify(String which, OfflinePlayer player, int adding) {
		String permission = "";
		String pattern = "([1-9][0-9]{0,3}|10000)";
		String world = (which.equalsIgnoreCase("plots") ? "creative" : null);
		int newLimit;

		switch (which) {
			case "homes":
				permission = "homes.limit.";
				break;
			case "npcs":
				permission = "citizens.npc.limit.";
				break;
			case "plots":
				permission = "plots.plot.";
				break;
			case "vaults":
				permission = "playervaults.amount.";
				break;
		}

		newLimit = getNewLimit(player, permission, world) + adding;

		BNCore.getPex().playerAdd(null, player, permission + newLimit);
		send(PREFIX + "New " + which + " limit for " + player.getName() + ": " + newLimit);
	}

	private static int getNewLimit(OfflinePlayer player, String permission, String world) {
		List<Integer> ints = new ArrayList<>();

		for (int i = 1; i <= 100; i++)
			if (BNCore.getPex().playerHas(world, player, permission + i)) {
				BNCore.getPex().playerRemove(null, player, permission + i);
				ints.add(i);
			}

		return ints.isEmpty() ? 0 : Collections.max(ints);
	}
}

