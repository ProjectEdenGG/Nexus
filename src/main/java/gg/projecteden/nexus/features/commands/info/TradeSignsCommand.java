package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.wiki._WikiSearchCommand.WikiType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class TradeSignsCommand extends CustomCommand {

	public TradeSignsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	public void help() {
		line();
		send(json("&eClick here &3to open the wiki on &eTrade Signs").url(WikiType.SERVER.getBasePath() + "Economy#Trade_Signs"));
		line();
	}
}
