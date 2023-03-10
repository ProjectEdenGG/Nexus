package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange;
import lombok.NonNull;

@Permission("set.my.rank")
@WikiConfig(rank = "Admin", feature = "Misc")
public class MyRankCommand extends CustomCommand {

	public MyRankCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<rank>")
	@Description("Set your rank")
	void set(Rank rank) {
		GroupChange.set().player(player()).group(rank).runAsync();
		send(PREFIX + "Set your rank to " + rank.getColoredName());
	}

}
