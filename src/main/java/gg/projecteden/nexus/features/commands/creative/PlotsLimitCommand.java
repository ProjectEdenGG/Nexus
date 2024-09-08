package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;

@HideFromWiki
public class PlotsLimitCommand extends CustomCommand {

	public PlotsLimitCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (worldGroup() != WorldGroup.SERVER && worldGroup() != WorldGroup.CREATIVE) {
			error("&cYou must be in the /creative world to use this command!");
		}

		int limit = 0;
		for (int i = 1; i <= 99; i++)
			if (hasPermission("plots.plot." + i))
				limit = i;

		if (limit == 0) {
			error("&cYou cannot claim any plots");
		}

		send("&3You can claim &e" + limit + plural(" &3plot", limit));
	}
}
