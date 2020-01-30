package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class TradeSignsCommand extends CustomCommand {

	public TradeSignsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line();
		json("&eClick here &3to open the wiki on &eTrade Signs||url:https://wiki.bnn.gg/wiki/Economy#Trade_Signs");
	}
}
