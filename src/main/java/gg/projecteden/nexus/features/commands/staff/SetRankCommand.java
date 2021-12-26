package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@Permission(Group.SENIOR_STAFF)
public class SetRankCommand extends CustomCommand {

	public SetRankCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <rank>")
	void set(OfflinePlayer player, Rank rank) {
		GroupChange.set().player(player).group(rank).runAsync();
		send(PREFIX + "Set " + player.getName() + "'s rank to " + rank.getColoredName());
	}

}
