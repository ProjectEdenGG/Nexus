package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class TradeSignsCommand extends CustomCommand {

	public TradeSignsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	public void help() {
		line();
		send(json("&eClick here &3to open the wiki on &eTrade Signs").url("https://wiki.projecteden.gg/wiki/Economy#Trade_Signs"));
		line();
	}
}
