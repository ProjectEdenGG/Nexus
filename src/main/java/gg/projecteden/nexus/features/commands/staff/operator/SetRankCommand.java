package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@Permission(Group.SENIOR_STAFF)
public class SetRankCommand extends CustomCommand {

	public SetRankCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Set a player's rank")
	void set(OfflinePlayer player, Rank rank) {
		GroupChange.set().player(player).group(rank).runAsync();
		send(PREFIX + "Set " + player.getName() + "'s rank to " + rank.getColoredName());
	}

}
