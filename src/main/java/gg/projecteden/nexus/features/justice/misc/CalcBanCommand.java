package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

import java.text.DecimalFormat;

@Permission(Group.MODERATOR)
public class CalcBanCommand extends _JusticeCommand {

	public CalcBanCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<pastBans> <blocksGriefed>")
	@Description("View a rough recommended ban time based on past bans and blocks griefed")
	void run(int bans, int blocksGriefed) {
		double hours = 0;
		if (++bans < 2)
			hours = bans * Math.sqrt(blocksGriefed) + (bans * 10) - 3;
		else if (bans < 3)
			hours = bans * Math.sqrt(blocksGriefed * 15) + (bans * 10);

		send();
		if (hours == 0)
			send("&eRecommended ban time: &cPermanent. Talk to an Operator.");
		else {
			if (hours > 24)
				send("&eRecommended ban time: &c" + new DecimalFormat("#.00").format(hours / 24) + " days");
			else
				send("&eRecommended ban time: &c" + Math.round(hours) + " hours");
			send("&3Please adjust for circumstances like the purpose of grief (gathering vs malicious), the player's overall attitude in the community, etc.");
		}

	}

}
