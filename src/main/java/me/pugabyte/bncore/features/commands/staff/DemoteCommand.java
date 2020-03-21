package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Permission("permissions.demote.default")
public class DemoteCommand extends CustomCommand {

	public DemoteCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> [rank]")
	void promote(Player player, String rank) {
		PermissionUser user = PermissionsEx.getUser(player);
		PermissionGroup[] ranks = user.getGroups();
		String oldRank = "";
		TEST:
		for (Rank rankTest : Rank.values()) {
			for (PermissionGroup group : ranks) {
				if (group.getName().equalsIgnoreCase(rankTest.name())) {
					if (!rankTest.isStaff()) {
						oldRank = group.getName();
						break TEST;
					}
				}
			}
		}
		if (oldRank.equalsIgnoreCase(Rank.GUEST.name())) error("User is already Guest");
		runCommandAsConsole("pex user " + player.getName() + " group remove " + oldRank);
		if (rank == null) {
			List<Rank> rankList = new ArrayList<>(Arrays.asList(Rank.values()));
			rank = rankList.get(rankList.indexOf(Rank.valueOf(oldRank.toUpperCase())) - 1).name();
		} else {
			try {
				rank = Rank.valueOf(rank.toUpperCase()).name();
			} catch (Exception e) {
				error("Invalid rank");
			}
		}
		runCommandAsConsole("pex user " + player.getName() + " group add " + rank);
		Utils.updatePrefix(player, user.getPrefix());
		send(PREFIX + "Demoted " + player.getName() + " to " + Rank.valueOf(rank).getFormat() + StringUtils.camelCase(rank));
	}

}
