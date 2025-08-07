package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@HideFromWiki
public class CommunityCouncilCommand extends CustomCommand {

	public CommunityCouncilCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(
			"&3The &eCommunity Council &3Discord role is given to long-time players who " +
			"have &eexcelled &3at helping out the server through &ecommunity participation&3, " +
			"&eprojects&3, and other means. Council members have access to several staff " +
			"channels where they can chime in &eideas for projects &3and help give input on " +
			"&enew features and events &3we're working on before they're released."
		);
	}
}
