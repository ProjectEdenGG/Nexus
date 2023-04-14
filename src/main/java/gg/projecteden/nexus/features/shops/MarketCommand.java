package gg.projecteden.nexus.features.shops;

import gg.projecteden.nexus.features.shops.providers.BrowseMarketProvider;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;

@HideFromWiki
public class MarketCommand extends CustomCommand {

	public MarketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	void run() {
		error("The Market has been retired. Replacement coming soon!");

		new BrowseMarketProvider(null).open(player());
	}

	@Path("reload")
	@Permission(Group.STAFF)
	void reload() {
		Market.load();
		send(PREFIX + "Market reloaded");
	}

}
