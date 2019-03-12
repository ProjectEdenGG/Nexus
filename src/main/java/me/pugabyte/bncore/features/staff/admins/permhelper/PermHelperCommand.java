package me.pugabyte.bncore.features.staff.admins.permhelper;

import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import ru.tehkode.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PermHelperCommand implements CommandExecutor {
	private final static String PREFIX = BNCore.getPrefix("PermHelper");

	PermHelperCommand() {
		BNCore.registerCommand("permhelper", this);
	}

	private static void modify(String which, CommandSender sender, PermissionUser user, int adding) {
		String permission = "";
		String pattern = (which.equalsIgnoreCase("homes") ? "(g{1,101})" : "([1-9][0-9]{0,3}|10000)");
		String world = (which.equalsIgnoreCase("plots") ? "creative" : "");
		String finalPermission;
		int newLimit;

		switch (which) {
			case "homes":
				permission = "essentials.sethome.multiple.";
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

		List<Integer> ints = new ArrayList<>();
		newLimit = getNewLimit(which, user, permission, pattern, world, adding);

		if (which.equalsIgnoreCase("homes")) {
			finalPermission = permission + StringUtils.repeat("g", newLimit);
		} else {
			finalPermission = permission + newLimit;
		}

		user.addPermission(finalPermission);
		sender.sendMessage(PREFIX + "New " + which + " limit for " + user.getName() + ": " + newLimit);
	}

	private static int getNewLimit(String which, PermissionUser user, String permission, String pattern, String world, int adding) {
		List<Integer> ints = new ArrayList<>();

		for (String _permission : user.getPermissions(world)) {
			Matcher matcher = Pattern.compile(permission.replaceAll("\\.", "\\.") + pattern + "$").matcher(_permission);
			if (matcher.find()) {
				if (which.equalsIgnoreCase("homes")) {
					ints.add(matcher.group(1).length());
				} else {
					ints.add(Integer.valueOf(matcher.group(1)));
				}
				user.removePermission(permission + matcher.group(1));
			}
		}

		int currentLimit = 0;
		if (!ints.isEmpty()) {
			currentLimit = Collections.max(ints);
		}
		return currentLimit + adding;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && !sender.hasPermission("permissions.manage")) {
			sender.sendMessage("No permission");
			return true;
		}

		if (!Stream.of("npcs", "homes", "plots", "vaults").anyMatch(args[0]::equalsIgnoreCase)
				|| !(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))
				|| !args[3].matches("\\d+")
				|| args.length != 4) {
			sender.sendMessage(PREFIX + "Correct usage: /permhelper <npcs|homes|plots|vaults> <add|remove> <player> <amount>");
			return true;
		}

		PermissionUser user = PermissionsEx.getUser(args[2]);
		int adding = Integer.parseInt(args[3]);
		if (args[1].equals("remove")) {
			adding = 0 - adding;
		}

		modify(args[0].toLowerCase(), sender, user, adding);

		return true;
	}
}

