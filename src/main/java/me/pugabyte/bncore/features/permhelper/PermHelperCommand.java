package me.pugabyte.bncore.features.permhelper;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		modify(arg(1).toLowerCase(), PermissionsEx.getUser(player.getName()), amount);
	}

	private void modify(String which, PermissionUser user, int adding) {
		String permission = "";
		String pattern = "([1-9][0-9]{0,3}|10000)";
		String world = (which.equalsIgnoreCase("plots") ? "creative" : "");
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

		newLimit = getNewLimit(user, permission, pattern, world, adding);

		user.addPermission(permission + newLimit);
		send(PREFIX + "New " + which + " limit for " + user.getName() + ": " + newLimit);
	}

	private static int getNewLimit(PermissionUser user, String permission, String pattern, String world, int adding) {
		List<Integer> ints = new ArrayList<>();

		for (String _permission : user.getPermissions(world)) {
			Matcher matcher = Pattern.compile(permission.replaceAll("\\.", "\\.") + pattern + "$").matcher(_permission);
			if (matcher.find()) {
				ints.add(Integer.valueOf(matcher.group(1)));
				user.removePermission(permission + matcher.group(1));
			}
		}

		int currentLimit = 0;
		if (!ints.isEmpty()) {
			currentLimit = Collections.max(ints);
		}
		return currentLimit + adding;
	}
}

