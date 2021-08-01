package gg.projecteden.nexus.features.shops;

import gg.projecteden.nexus.features.shops.providers.BrowseMarketProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

public class MarketCommand extends CustomCommand {

	public MarketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		new BrowseMarketProvider(null).open(player());
	}

	@Path("reload")
	@Permission("group.staff")
	void reload() {
		Market.load();
		send(PREFIX + "Market reloaded");
	}

}
