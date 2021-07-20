package me.pugabyte.nexus.features.shops;

import lombok.NonNull;
import me.pugabyte.nexus.features.shops.Shops.Market;
import me.pugabyte.nexus.features.shops.providers.BrowseMarketProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

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
