package me.pugabyte.bncore.features.staff.admins.permhelper;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import ru.tehkode.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aliases("permhelper")
@Permission("permissions.manage")
@NoArgsConstructor
public class PermHelperCommand extends CustomCommand {
	String test = "hi";

	PermHelperCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		reply(PREFIX + "Correct usage: /permhelper <npcs|homes|plots|vaults> <add|remove> <player> <amount>");
	}

	@Path("(npcs|homes|plots|vaults) (add|remove) {offlineplayer} {int}")
	void modify(@Arg OfflinePlayer player, @Arg int amount) {
		PermissionUser user = PermissionsEx.getUser(player.getName());
		if (arg(2).equals("remove")) {
			amount = 0 - amount;
		}

		modify(arg(1).toLowerCase(), user, amount);
	}

	private void modify(String which, PermissionUser user, int adding) {
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
		reply(PREFIX + "New " + which + " limit for " + user.getName() + ": " + newLimit);
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
}

