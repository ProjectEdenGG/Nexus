package me.pugabyte.bncore.features.shops;

import lombok.NonNull;
import me.pugabyte.bncore.features.shops.providers.BrowseMarketProvider;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class MarketCommand extends CustomCommand {

	public MarketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		new BrowseMarketProvider(null).open(player());
	}

}
