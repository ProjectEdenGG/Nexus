package gg.projecteden.nexus.features.shops;

import gg.projecteden.nexus.features.shops.providers.BrowseMarketProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import lombok.NonNull;

@HideFromWiki
public class MarketCommand extends CustomCommand {

	public MarketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (!SubWorldGroup.RESOURCE.contains(world()))
			error("This command is only available in the resource world");

		new BrowseMarketProvider(null).open(player());
	}

	@Path("reload")
	@Permission(Group.STAFF)
	void reload() {
		Market.load();
		send(PREFIX + "Market reloaded");
	}

}
